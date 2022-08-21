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
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          pickupdropouthandler=Custom('pickupdropouthandler','./qakicons/symActorSmall.png')
          mover=Custom('mover','./qakicons/symActorSmall.png')
          moveruturn=Custom('moveruturn','./qakicons/symActorSmall.png')
          basicrobotwrapper=Custom('basicrobotwrapper','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          distancefilter=Custom('distancefilter(coded)','./qakicons/codedQActor.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          led_alarm_control=Custom('led_alarm_control','./qakicons/symActorSmall.png')
          led=Custom('led','./qakicons/symActorSmall.png')
          alarmemitter=Custom('alarmemitter','./qakicons/symActorSmall.png')
          sonar=Custom('sonar','./qakicons/symActorSmall.png')
     smartdevice >> Edge(color='magenta', style='solid', xlabel='depositrequest') >> wasteservice
     wasteservice >> Edge(color='magenta', style='solid', xlabel='depositaction') >> depositaction
     depositaction >> Edge(color='blue', style='solid', xlabel='err') >> wasteservice
     depositaction >> Edge(color='magenta', style='solid', xlabel='move') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='pickup') >> transporttrolley
     depositaction >> Edge(color='magenta', style='solid', xlabel='dropout') >> transporttrolley
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='pickup') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='dropout') >> pickupdropouthandler
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='move') >> mover
     sys >> Edge(color='red', style='dashed', xlabel='alarm') >> pickupdropouthandler
     sys >> Edge(color='red', style='dashed', xlabel='alarmceased') >> pickupdropouthandler
     mover >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     mover >> Edge(color='magenta', style='solid', xlabel='moveruturn') >> moveruturn
     moveruturn >> Edge(color='magenta', style='solid', xlabel='cmdsync') >> basicrobotwrapper
     sys >> Edge(color='red', style='dashed', xlabel='alarm') >> basicrobotwrapper
     sys >> Edge(color='red', style='dashed', xlabel='alarmceased') >> basicrobotwrapper
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     sys >> Edge(color='red', style='dashed', xlabel='info') >> basicrobotwrapper
     basicrobot >> Edge( xlabel='info', **eventedgeattr) >> sys
     basicrobotwrapper >> Edge(color='blue', style='solid', xlabel='coapUpdate') >> led_alarm_control
     mover >> Edge(color='blue', style='solid', xlabel='coapUpdate') >> led_alarm_control
     pickupdropouthandler >> Edge(color='blue', style='solid', xlabel='coapUpdate') >> led_alarm_control
     led_alarm_control >> Edge( xlabel='update_led', **eventedgeattr) >> sys
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
     sys >> Edge(color='red', style='dashed', xlabel='local_sonardata') >> alarmemitter
     alarmemitter >> Edge( xlabel='alarm', **eventedgeattr) >> sys
     alarmemitter >> Edge( xlabel='alarmceased', **eventedgeattr) >> sys
     sonar >> Edge( xlabel='local_sonardata', **eventedgeattr) >> sys
diag
