# nextflow-io/nf-schema: Changelog

# Version 2.1.0

## Breaking changes

1. The minimum supported Nextflow version is now `23.10.0` instead of `22.10.0`

## New features

1. The plugin now fully supports nested parameters!

## Help message changes

1. The use of the `paramsHelp()` function has now been deprecated in favor of a new built-in help message functionality. `paramsHelp()` has been updated to use the reworked help message creator. If you still want to use `paramsHelp()` for some reason in your pipeline, please add the `hideWarning:true` option to it to make sure the deprecation warning will not be shown.
2. Added new configuration values to support the new help message functionality:
    - `validation.help.enabled`: Enables the checker for the help message parameters. The plugin will automatically show the help message when one of these parameters have been given and exit the pipeline. Default = `false`
    - `validation.help.shortParameter`: The parameter to use for the compact help message. This help message will only contain top level parameters. Default = `help`
    - `validation.help.fullParameter`: The parameter to use for the expanded help message. This help message will show all parameters no matter how deeply nested they are. Default = `helpFull`
    - `validation.help.showHiddenParameter`: The parameter to use to also show all parameters with the `hidden: true` keyword in the schema. Default = `showHidden`
    - `validation.help.showHidden`: Set this to `true` to show hidden parameters by default. This configuration option is overwritten by the value supplied to the parameter in `validation.help.showHiddenParameter`. Default = `false`
    - `validation.help.beforeText`: Some custom text to add before the help message.
    - `validation.help.afterText`: Some custom text to add after the help message.
    - `validation.help.command`: An example command to add to the top of the help message
3. Added support for nested parameters to the help message. A detailed help message using `--help <parameter>` will now also contain all nested parameters. The parameter supplied to `--help` can be a nested parameter too (e.g. `--help top_parameter.nested_parameter.deeper_parameter`)
4. The help message now won't show empty parameter groups.

## JSON schema fixes

1. The `defs` keyword is now deprecated in favor of the `$defs` keyword. This to follow the JSON schema guidelines. We will continue supporting `defs` for backwards compatibility.

# Version 2.0.1 - Tsukemen

## Vulnerability fix

1. Updated the org.json package to version `20240303`.

# Version 2.0.0 - Kagoshima

To migrate from nf-validation please follow the [migration guide](https://nextflow-io.github.io/nf-schema/latest/migration_guide/)

## New features

- Added the `uniqueEntries` keyword. This keyword takes a list of strings corresponding to names of fields that need to be a unique combination. e.g. `uniqueEntries: ['sample', 'replicate']` will make sure that the combination of the `sample` and `replicate` fields is unique. ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- Added `samplesheetToList` which is the function equivalent of `.fromSamplesheet` [#3](https://github.com/nextflow-io/nf-schema/pull/3)
- Added a warning if the `nf-schema` version is unpinned. Let's hope this prevents future disasters like the release of `nf-validation` v2.0 :grin:

## Changes

- Changed the used draft for the schema from `draft-07` to `draft-2020-12`. See the [2019-09](https://json-schema.org/draft/2019-09/release-notes) and [2020-12](https://json-schema.org/draft/2020-12/release-notes) release notes for all changes ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- Removed the `fromSamplesheet` channel operator and added a `samplesheetToList` function instead. This function validates the samplesheet and returns a list of it. [#3](https://github.com/nextflow-io/nf-schema/pull/3)
- Removed the `unique` keyword from the samplesheet schema. You should now use [`uniqueItems`](https://json-schema.org/understanding-json-schema/reference/array#uniqueItems) or `uniqueEntries` instead ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- Removed the `skip_duplicate_check` option from the `samplesheetToList()` function and the `--validationSkipDuplicateCheck` parameter. You should now use the `uniqueEntries` or [`uniqueItems`](https://json-schema.org/understanding-json-schema/reference/array#uniqueItems) keywords in the schema instead ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- `samplesheetToList()` now does dynamic typecasting instead of using the `type` fields in the JSON schema. This is done due to the complexity of `draft-2020-12` JSON schemas. This should not have that much impact but keep in mind that some types can be different between this version and older versions in nf-validation ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- `samplesheetToList()` will now set all missing values as `[]` instead of the type specific defaults (because of the changes in the previous point). This should not change that much as this will also result in `false` when used in conditions. ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- Removed the configuration parameters and added configuration options instead. For a full list of these new options, please have a look at the [configuration docs](https://nextflow-io.github.io/nf-schema/latest/configuration/)
- Ignore validation of Azure and GCP hosted blob storage files in addition to AWS S3 hosted files. This is because they are not true POSIX compliant files and would incorrectly fail validation ([#29](https://github.com/nextflow-io/nf-schema/pull/29))

## Improvements

- Setting the `exists` keyword to `false` will now check if the path does not exist ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- The `schema` keyword will now work in all schemas. ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- Improved the error messages ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
- `.fromSamplesheet()` now supports deeply nested samplesheets ([#141](https://github.com/nextflow-io/nf-validation/pull/141))
