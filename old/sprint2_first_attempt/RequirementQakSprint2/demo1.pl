%====================================================================================
% demo1 description   
%====================================================================================
mqttBroker("broker.hivemq.com", "1883", "unibo/nat/radar").
context(ctxsonar, "localhost",  "TCP", "8095").
 qactor( sonarsimulator, ctxsonar, "sonarSimulator").
  qactor( sonardatasource, ctxsonar, "sonarHCSR04Support2021").
  qactor( datacleaner, ctxsonar, "dataCleaner").
  qactor( sonarqak22, ctxsonar, "it.unibo.sonarqak22.Sonarqak22").
