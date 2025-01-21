package nextflow.validation.utils

import org.json.JSONPointer
import org.json.JSONPointerException
import groovy.util.logging.Slf4j
import java.nio.file.Path

/**
 * A collection of commonly used functions
 *
 * @author : mirpedrol <mirp.julia@gmail.com>
 * @author : nvnieuwk <nicolas.vannieuwkerke@ugent.be>
 * @author : KevinMenden
 */

@Slf4j
public class Common {

    //
    // Get full path based on the base directory of the pipeline run
    //
    public static String getBasePath(String baseDir, String schemaFilename) {
        if (Path.of(schemaFilename).exists()) {
            return schemaFilename
        } else {
            return "${baseDir}/${schemaFilename}"
        }
    }

    //
    // Function to get the value from a JSON pointer
    //
    public static Object getValueFromJsonPointer(String jsonPointer, Object json) {
        def JSONPointer schemaPointer = new JSONPointer(jsonPointer)
        try {
            return schemaPointer.queryFrom(json) ?: ""
        } catch (JSONPointerException e) {
            return ""
        }
    }

    //
    // Get the amount of character of the largest key in a map
    //
    public static Integer getLongestKeyLength( Map input ) {
        return Collections.max(input.collect { _, val -> 
            def Map groupParams = val as Map
            longestStringLength(groupParams.keySet() as List<String> )
        })
    }

    //
    // Get the size of the longest string value in a list of strings
    //
    public static Integer longestStringLength( List<String> strings ) {
        return strings ? Collections.max(strings.collect { it.size() }) : 0
    }

    //
    // Find a value in a nested map
    //
    public static Object findDeep(Object m, String key) {
        if (m instanceof Map) {
            if (m.containsKey(key)) {
                return m[key]
            } else {
                return m.findResult { k, v -> v instanceof Map ? findDeep(v, key) : null }
            }
        } 
        else if (m instanceof List) {
            def result = null
            m.each {
                def res = findDeep(it)
                if (res != null) {
                    result = res
                    return
                }
            }
            return result
        }
        return null
    }
}