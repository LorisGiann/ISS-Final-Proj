%====================================================================================
% demo0 description   
%====================================================================================
context(ctxsonar, "localhost",  "TCP", "8095").
 qactor( sonar, ctxsonar, "it.unibo.sonar.Sonar").
  qactor( test_sonar, ctxsonar, "it.unibo.test_sonar.Test_sonar").
