Flow SoapUI Plugin
-------------------------------

*Only tested on SoapUI OS 5.3.0*

Tiny plugin to give the user the possibility to have unstable test-steps primarily by allowing them to fail and pass through a repetition procedure. But i intend to add new test-steps for having just control-flow in testCases. 

INSTALL
-------------------------------

```bash
  mvn clean install 
```

copy the ``` target/flow-soapui-plugin-1.0.0.jar ``` and put it in ``` ${USER_HOME}/.soapuios/plugins```

UNSIGNED PLUGIN
-------------------------------

This plugin is an unsigned SoapUI plugin for now and until it is signed then you will have to use 
hschotts Jailbreak Plugin

```
   https://github.com/hschott/soapui-pluginloader-jailbreak
```