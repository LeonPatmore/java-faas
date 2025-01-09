
export EVENT_SOURCE_WEB_ENABLED=true
./java -Dloader.path=C:/Users/Leon-/Documents/GitHub/java-faas/example/build/libs/example-0.0.1-SNAPSHOT-plain.jar -cp 'C:\Users\Leon-\Documents\GitHub\java-faas\core\core\build\libs\core-0.0.1-SNAPSHOT.jar;' org.springframework.boot.loader.launch.PropertiesLauncher

./jar tf 'C:\Users\Leon-\Documents\GitHub\java-faas\core\core\build\libs\core-0.0.1-SNAPSHOT.jar'

export MSYS_NO_PATHCONV=1


``` 
source:
    factory: webEventSourceFactory
    props:
        path: /path
```
