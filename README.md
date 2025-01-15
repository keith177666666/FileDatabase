## File Data Base
# introduction
This database is based on the 
[database](https://github.com/keith177666666/Database-java)
created by me.
# using
There are a Example in this [file](/src/main/java/dev/keith/Example.java).

# Importing
```groovy
repositories {
    // Other repo
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.keith177666666:FileDatabase:v${DB_VERSION}'
}
```
This should be in either your gradle.properties or your build.gradle
```groovy
let DB_VERSION = "YOUR DB VERSION"
```
db version : [![](https://jitpack.io/v/keith177666666/FileDatabase.svg)](https://jitpack.io/#keith177666666/FileDatabase)
db java doc : [javadoc](https://keith177666666.github.io/FileDataBase.io/index.html)