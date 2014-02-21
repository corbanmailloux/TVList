del .\*.jar
del .\*.java
del .\*.class
del .\*.mf
copy ..\*.java .\
copy ..\*.properties .\
copy .\config\*.* .\
javac *.java
jar cvfm TVList.jar manifest.mf *.class
del .\*.java
del .\*.class
del .\*.mf
pause
