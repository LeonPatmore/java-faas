
./java -Dloader.path=C:/Users/Leon-/Documents/GitHub/java-faas/example/build/libs/example-0.0.1-SNAPSHOT-plain.jar -cp 'C:\Users\Leon-\Documents\GitHub\java-faas\core\build\libs\core-0.0.1-SNAPSHOT.jar;' org.springframework.boot.loader.launch.PropertiesLauncher

./java -cp 'C:\Users\Leon-\Documents\GitHub\java-faas\core\build\libs\core-0.0.1-SNAPSHOT.jar' org.springframework.boot.loader.launch.JarLauncher

./jar tf 'C:\Users\Leon-\Documents\GitHub\java-faas\example\build\libs\example-0.0.1-SNAPSHOT-plain.jar'   
./jar tf 'C:\Users\Leon-\Documents\GitHub\java-faas\core\build\libs\core-0.0.1-SNAPSHOT.jar'


``` 
source:
    factory: webEventSourceFactory
    props:
        path: /path
```
