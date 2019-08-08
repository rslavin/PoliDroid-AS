# PoliDroid-AS
Android Studio plugin for the detection of potential privacy policy misalignments. PoliDroid-AS works by scanning your code in real time and detecting policy violations based on a mapping between Android API methods and a mobile privacy lexicon. Weak and strong violations are identified based on an OWL mobile privacy ontology.

For more information, visit http://polidroid.org.

### Installation
Though PoliDroid-AS is designed for Android Studio, it can be installed on any IDE based on IntelliJ. 

To install, simply navitate to File > Settings > Plugins and click the "Install plugin from disk..." button at the bottom and select the PoliDroid-AS jar file. Once installed, PoliDroid-AS can be configured from the Editor > Inspections menu in the IDE's settings.

### Developer Notes
_After_ importing as a Gradle project, you'll need to add a run/debug Configuration. Click add Configuration in the toolbar menu and add a Gradle configuration. For the project field choose PoliDroid-AS and in the task field type ":runIde". 

