# MovieApp
1. You can add your own API KEY under local.properties 
OR
2. Simply delete these files below under app build.gradle and hard code API_KEY in MainActivity under variable myApiKey.

def localPropertiesFile = rootProject.file("local.properties")
def localProperties = new Properties()
localProperties.load(new FileInputStream(localPropertiesFile))

android {
    defaultConfig {
        buildConfigField "String", "API_KEY", localProperties['apiKey']
    }
