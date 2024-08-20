package nextflow.validation

import groovy.util.logging.Slf4j
import groovy.transform.PackageScope


/**
 * This class allows to model a specific configuration, extracting values from a map and converting
 *
 * We anotate this class as @PackageScope to restrict the access of their methods only to class in the
 * same package
 *
 * @author : nvnieuwk <nicolas.vannieuwkerke@ugent.be>
 *
 */

@Slf4j
@PackageScope
class SummaryConfig {
    final public String beforeText
    final public String afterText

    SummaryConfig(Map map) {
        def config = map ?: Collections.emptyMap()
        beforeText  = config.beforeText ?: ""
        afterText   = config.afterText  ?: ""
    }
}