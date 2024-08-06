package nextflow.validation

import groovy.util.logging.Slf4j

import java.nio.file.Path

import nextflow.Session

/**
 * This class contains methods to write a help message
 *
 * @author : nvnieuwk <nicolas.vannieuwkerke@ugent.be>
 *
 */

@Slf4j
class HelpMessage {

    private final ValidationConfig config
    private final Map colors
    private Integer hiddenParametersCount = 0
    private Map<String,Map> paramsMap
    private Integer maxChars

    // The length of the terminal
    private Integer terminalLength = System.getenv("COLUMNS")?.toInteger() ?: 100

    HelpMessage(ValidationConfig config, Session session) {
        config = config
        colors = Utils.logColours(config.monochromeLogs)
        paramsMap = Utils.paramsLoad( Path.of(Utils.getSchemaPath(session.baseDir.toString(), config.parametersSchema)) )
        maxChars = Utils.paramsMaxChars(paramsMap) + 1
    }

    public printShortHelpMessage(String param) {
        if (param) {
            def List<String> paramNames = param.tokenize(".") as List<String>
            def Map paramOptions = [:]
            for (group in paramsMap.keySet()) {
                def Map groupParams = paramsMap.get(group) as Map // This gets the parameters of that particular group
                if (groupParams.containsKey(paramNames[0])) {
                    paramOptions = groupParams.get(paramNames[0]) as Map 
                }
            }
            if (paramNames.size() > 1) {
                paramNames.remove(0)
                paramNames.each {
                    paramOptions = (Map) paramOptions?.properties?[it] ?: [:]
                }
            }
            if (!paramOptions) {
                throw new Exception("Specified param '${paramName}' does not exist in JSON schema.")
            }
            log.info(getDetailedHelpString(param, paramOptions, colors))
        }
        log.info("Short help message")
    }

    public printFullHelpMessage() {
        log.info("Full help message")
    }

    private Map<String, Map> getHelpMap() {
        helpMap = [:]
        
        return helpMap
    }

    //
    // Get a detailed help string from one parameter
    //
    private String getDetailedHelpString(String paramName, Map paramOptions, Map colors) {
        def String helpMessage = "--" + paramName + '\n'
        for (option in paramOptions) {
            def String key = option.key
            if (key == "fa_icon" || (key == "type" && option.value == "object")) {
                continue
            }
            if (key == "properties") {
                def Map subParamsOptions = [:]
                flattenNestedSchemaMap(option.value as Map).each { String subParam, Map value ->
                    subParamsOptions.put("${paramName}.${subParam}" as String, value)
                }
                def Integer maxChars = Utils.paramsMaxChars(subParamsOptions, true) + 1
                def String subParamsHelpString = getHelpList(subParamsOptions, colors, maxChars, paramName)
                    .collect {
                        "      --" + it[4..it.length()-1]
                    }
                    .join("\n")
                helpMessage += "    " + colors.dim + "options".padRight(11) + ": " + colors.reset + "\n" + subParamsHelpString + "\n"
                continue
            }
            def String value = option.value
            if (value.length() > terminalLength) {
                value = wrapText(value)
            }
            helpMessage += "    " + colors.dim + key.padRight(11) + ": " + colors.reset + value + '\n'
        }
        return helpMessage
    }

    //
    // Get help text in string format
    //
    private List<String> getHelpList(Map<String,Map> params, Map colors, Integer maxChars, String parentParameter = "") {
        def List helpMessage = []
        for (String paramName in params.keySet()) {
            def Map paramOptions = params.get(paramName) as Map 
            if (paramOptions.hidden && !config.showHiddenParams) {
                hiddenParametersCount += 1
                continue
            }
            def String type = '[' + paramOptions.type + ']'
            def String enumsString = ""
            if (paramOptions.enum != null) {
                def List enums = (List) paramOptions.enum
                def String chopEnums = enums.join(", ")
                if(chopEnums.length() > terminalLength){
                    chopEnums = chopEnums.substring(0, terminalLength-5)
                    chopEnums = chopEnums.substring(0, chopEnums.lastIndexOf(",")) + ", ..."
                }
                enumsString = " (accepted: " + chopEnums + ") "
            }
            def String description = paramOptions.description ? paramOptions.description as String + " " : ""
            def defaultValue = paramOptions.default != null ? "[default: " + paramOptions.default.toString() + "] " : ''
            def String nestedParamName = parentParameter ? parentParameter + "." + paramName : paramName
            def String nestedString = paramOptions.properties ? "(This parameter has sub-parameters. Use '--help ${nestedParamName}' to see all sub-parameters) " : ""
            def descriptionDefault = description + colors.dim + enumsString + defaultValue + colors.reset + nestedString
            // Wrap long description texts
            // Loosely based on https://dzone.com/articles/groovy-plain-text-word-wrap
            if (descriptionDefault.length() > terminalLength){
                descriptionDefault = wrapText(descriptionDefault)
            }
            helpMessage.add("  --" +  paramName.padRight(maxChars) + colors.dim + type.padRight(10) + colors.reset + descriptionDefault)
        }
        return helpMessage
    }

    //
    // Flattens the schema params map so all nested parameters are shown as their full name
    //
    private Map<String,Map> flattenNestedSchemaMap(Map params) {
        def Map returnMap = [:]
        params.each { String key, Map value ->
            if (value.containsKey("properties")) {
                def flattenedMap = flattenNestedSchemaMap(value.properties)
                flattenedMap.each { String k, Map v ->
                    returnMap.put(key + "." + k, v)
                }
            } else {
                returnMap.put(key, value)
            }
        }
        return returnMap
    }

}