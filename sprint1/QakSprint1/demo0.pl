%====================================================================================
% demo0 description   
%====================================================================================
context(ctxserver, "localhost",  "TCP", "8095").
context(ctxrobot, "127.0.0.1",  "TCP", "8096").
 qactor( wasteservice, ctxserver, "it.unibo.wasteservice.Wasteservice").
  qactor( transporttrolley, ctxrobot, "it.unibo.transporttrolley.Transporttrolley").
