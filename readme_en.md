# Call Sub-pipeline
Start and run the pipeline under the specified project synchronously/asynchronously

### Function Description

- Call the pipeline started by the user, which can be executed synchronously or asynchronously. When calling the pipeline, you can set the corresponding parameters of the pipeline. Otherwise, the pipeline will be executed with the default value of the parameters.

### Notes on use

- Users can only call pipelines authorized

- When using this plug-in, this plug-in should not be used in multiple pipelines if the pipelines directly call each other in a loop. Otherwise, the plug-in will fail to execute directly.

### Sub-pipeline parameter description

- When a sub-pipeline has startup parameters, the plug-in displays the sub-pipeline startup parameters and provides default values

- Except for the multi-select box type parameters, other parameters are modified in the form of input boxes in the plug-in

- The default polling interval is 10s

- The sub-pipeline output variable namespace defaults to sub_pipeline_, if the end character of the custom namespace is not an underscore, it will be added automatically

- When obtaining multiple sub-pipeline output variables, separate them with English commas

### FAQ
