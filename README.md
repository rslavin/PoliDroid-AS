# PoliDroid-AS
Android Studio plugin for the detection of potential privacy policy misalignments. PoliDroid-AS works by scanning your code in real time and detecting policy violations based on a mapping between Android API methods and a mobile privacy lexicon. Weak and soft violations are identified based on an OWL mobile privacy ontology.

For more information, visit http://polidroid.org.

### Installation
Though PoliDroid-AS is designed for Android Studio, it can be installed on any IDE based on IntelliJ. 

To install, simply navitate to File > Settings > Plugins and click the "Install plugin from disk..." button at the bottom and select the PoliDroid-AS jar file. Once installed, PoliDroid-AS can be configured from the Editor > Inspections menu in the IDE's settings.

### Developer Notes
_After_ importing as a Maven project and downloading dependencies (and probably restarting the IDE), you'll need to change `type="JAVA_MODULE"` to `type="PLUGIN_MODULE"` in your .iml file. Then you'll be able to find it in the "Use classpath of module" dropdown in your run configuration. 
Also, make sure META-INF is set: Project settings > Modules > [plugin] > Plugin Deployment > Path to META-INF/plugin.xml

Maven projects aren't currently supported by the IntelliJ SDK so there may be other minor issues.
