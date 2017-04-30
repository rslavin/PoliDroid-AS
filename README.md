# PoliDroid-AS
Android Studio plugin for the detection of potential privacy policy misalignments. PoliDroid-AS works by scanning your code in real time and detecting policy violations based on a mapping between Android API methods and a mobile privacy lexicon. Weak and soft violations are identified based on an OWL mobile privacy ontology.

# Developer Notes
When importing from Git repository, you'll need to change type="JAVA_MODULE" to type="PLUGIN_MODULE" in your .iml file. Then you'll be able to find it in the "Use classpath of module" dropdown in your run configuration. Also, set url="file://$MODULE_DIR$/META-INF/plugin.xml" in the component tag.

Make sure META-INF is set: Project settings > Modules > [plugin] > Plugin Deployment > Path to META-INF/plugin.xml
