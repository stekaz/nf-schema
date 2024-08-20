package nextflow.validation

import groovy.util.logging.Slf4j
import groovy.transform.PackageScope


/**
 * This class allows model an specific configuration, extracting values from a map and converting
 *
 * We anotate this class as @PackageScope to restrict the access of their methods only to class in the
 * same package
 *
 * @author : nvnieuwk <nicolas.vannieuwkerke@ugent.be>
 *
 */

@Slf4j
@PackageScope
class ValidationConfig {

    final public Boolean lenientMode
    final public Boolean monochromeLogs
    final public Boolean failUnrecognisedParams
    final public String  parametersSchema
    final public Boolean showHiddenParams = false
    final public HelpConfig help
    final public SummaryConfig summary

    final public List<String> ignoreParams

    ValidationConfig(Map map, Map params){
        def config = map ?: Collections.emptyMap()
        lenientMode             = config.lenientMode            ?: false
        monochromeLogs          = config.monochromeLogs         ?: false
        failUnrecognisedParams  = config.failUnrecognisedParams ?: false
        if(config.showHiddenParams) {
            log.warn("configuration option `validation.showHiddenParams` is deprecated, please use `validation.help.showHidden` or the `--showHidden` parameter instead")
        }
        parametersSchema        = config.parametersSchema       ?: "nextflow_schema.json"
        help                    = new HelpConfig(config.help as Map ?: [:], params)
        summary                 = new SummaryConfig(config.summary as Map ?: [:])

        if(config.ignoreParams && !(config.ignoreParams instanceof List<String>)) {
            throw new SchemaValidationException("Config value 'validation.ignoreParams' should be a list of String values")
        }
        ignoreParams = config.ignoreParams ?: []
        if(config.defaultIgnoreParams && !(config.defaultIgnoreParams instanceof List<String>)) {
            throw new SchemaValidationException("Config value 'validation.defaultIgnoreParams' should be a list of String values")
        }
        ignoreParams += config.defaultIgnoreParams ?: []
    }

    String getPrefix() { prefix }
}