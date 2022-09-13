from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
with Diagram('demosystemarchitectureArch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxdriver', graph_attr=nodeattr):
          smartdevice=Custom('smartdevice','./qakicons/symActorSmall.png')
     with Cluster('ctxserver', graph_attr=nodeattr):
          wasteservice=Custom('wasteservice','./qakicons/symActorSmall.png')
          depositaction=Custom('depositaction','./qakicons/symActorSmall.png')
     with Cluster('ctxrobot', graph_attr=nodeattr):
          transporttrolleystate=Custom('transporttrolleystate','./qakicons/symActorSmall.png')
          guiserver=Custom('guiserver','./qakicons/symActorSmall.png')
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          pickupdropouthandler=Custom('pickupdropouthandler','./qakicons/symActorSmall.png')
          mover=Custom('mover','./qakicons/symActorSmall.png')
          basicrobotwrapper=Custom('basicrobotwrapper','./qakicons/symActorSmall.png')
          basicrobotlorisdavide=Custom('basicrobotlorisdavide','./qakicons/symActorSmall.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          sonarlorisdavide=Custom('sonarlorisdavide','./qakicons/symActorSmall.png')
          alarmemitter=Custom('alarmemitter','./qakicons/symActorSmall.png')
          led=Custom('led','./qakicons/symActorSmall.png')
     transporttrolley >> Edge(color='blue', style='solid', xlabel='coapUpdate', fontcolor='blue') >> transporttrolleystate
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='coapUpdate', fontcolor='blue') >> transporttrolleystate
     pickupdropouthandler >> Edge(color='blue', style='solid', xlabel='coapUpdate', fontcolor='blue') >> transporttrolleystate
     transporttrolleystate >> Edge(color='blue', style='solid', xlabel='coapUpdate', fontcolor='blue') >> guiserver
     wasteservice >> Edge(color='blue', style='solid', xlabel='coapUpdate', fontcolor='blue') >> guiserver
     mover >> Edge(color='blue', style='solid', xlabel='coapUpdate', fontcolor='blue') >> guiserver
     led >> Edge(color='blue', style='solid', xlabel='coapUpdate', fontcolor='blue') >> guiserver
     smartdevice >> Edge(color='magenta', style='solid', xlabel='depositrequest', fontcolor='magenta') >> wasteservice
     wasteservice >> Edge(color='darkgreen', style='dashed', xlabel='loadrejected', fontcolor='darkgreen') >> smartdevice
     wasteservice >> Edge(color='magenta', style='solid', xlabel='depositaction', fontcolor='magenta') >> depositaction
     wasteservice >> Edge(color='darkgreen', style='dashed', xlabel='loadaccept', fontcolor='darkgreen') >> smartdevice
     depositaction >> Edge(color='blue', style='solid', xlabel='err', fontcolor='blue') >> wasteservice
     depositaction >> Edge(color='magenta', style='solid', xlabel='moveto', fontcolor='magenta') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='pickup', fontcolor='magenta') >> transporttrolley
     depositaction >> Edge(color='darkgreen', style='dashed', xlabel='pickupdone', fontcolor='darkgreen') >> wasteservice
     depositaction >> Edge(color='magenta', style='solid', xlabel='dropout', fontcolor='magenta') >> transporttrolley
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='pickup', fontcolor='magenta') >> pickupdropouthandler
     transporttrolley >> Edge(color='darkgreen', style='dashed', xlabel='pickupanswer', fontcolor='darkgreen') >> depositaction
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='dropout', fontcolor='magenta') >> pickupdropouthandler
     transporttrolley >> Edge(color='darkgreen', style='dashed', xlabel='dropoutanswer', fontcolor='darkgreen') >> depositaction
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='moveto', fontcolor='magenta') >> mover
     transporttrolley >> Edge(color='darkgreen', style='dashed', xlabel='movetoanswer', fontcolor='darkgreen') >> depositaction
     pickupdropouthandler >> Edge(color='darkgreen', style='dashed', xlabel='dropoutanswer', fontcolor='darkgreen') >> transporttrolley
     pickupdropouthandler >> Edge(color='darkgreen', style='dashed', xlabel='pickupanswer', fontcolor='darkgreen') >> transporttrolley
     mover >> Edge(color='darkgreen', style='dashed', xlabel='movetoanswer', fontcolor='darkgreen') >> transporttrolley
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync', fontcolor='magenta') >> basicrobotwrapper
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='cmd', fontcolor='blue') >> basicrobotlorisdavide
     basicrobotwrapper >> Edge(color='darkgreen', style='dashed', xlabel='cmdanswer', fontcolor='darkgreen') >> mover
     sys >> Edge(color='red', style='dashed', xlabel='info', fontcolor='red') >> basicrobotwrapper
     basicrobotlorisdavide >> Edge( xlabel='info', **eventedgeattr, fontcolor='red') >> sys
     sys >> Edge(color='red', style='dashed', xlabel='alarmsonar', fontcolor='red') >> sonarlorisdavide
     sonarlorisdavide >> Edge( xlabel='local_sonardata', **eventedgeattr, fontcolor='red') >> sys
     alarmemitter >> Edge( xlabel='alarm', **eventedgeattr, fontcolor='red') >> sys
     alarmemitter >> Edge( xlabel='alarmceased', **eventedgeattr, fontcolor='red') >> sys
     sys >> Edge(color='red', style='dashed', xlabel='local_sonardata', fontcolor='red') >> alarmemitter
     sys >> Edge(color='red', style='dashed', xlabel='update_led', fontcolor='red') >> led
diag
