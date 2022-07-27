%====================================================================================
% demo0 description   
%====================================================================================
context(ctxtest, "localhost",  "TCP", "8095").
 qactor( component, ctxtest, "it.unibo.component.Component").
  qactor( test_component, ctxtest, "it.unibo.test_component.Test_component").
