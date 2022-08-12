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
          guiserver=Custom('guiserver','./qakicons/symActorSmall.png')
     with Cluster('ctxrobot', graph_attr=nodeattr):
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          led=Custom('led','./qakicons/symActorSmall.png')
          sonar=Custom('sonar','./qakicons/symActorSmall.png')
     smartdevice >> Edge(color='magenta', style='solid', xlabel='depositrequest') >> wasteservice
     wasteservice >> Edge(color='magenta', style='solid', xlabel='transporttrolleycmd') >> transporttrolley
     wasteservice >> Edge( xlabel='update_container', **eventedgeattr) >> sys
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='step') >> basicrobot
     transporttrolley >> Edge(color='blue', style='solid', xlabel='cmd') >> basicrobot
     transporttrolley >> Edge( xlabel='update_tt_state', **eventedgeattr) >> sys
     transporttrolley >> Edge( xlabel='update_position', **eventedgeattr) >> sys
     sys >> Edge(color='red', style='dashed', xlabel='obstacle') >> transporttrolley
     sys >> Edge(color='red', style='dashed', xlabel='info') >> transporttrolley
     basicrobot >> Edge( xlabel='info', **eventedgeattr) >> sys
     sys >> Edge(color='red', style='dashed', xlabel='update_tt_state') >> guiserver
     sys >> Edge(color='red', style='dashed', xlabel='update_position') >> guiserver
     sys >> Edge(color='red', style='dashed', xlabel='update_container') >> guiserver
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> guiserver
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
     sonar >> Edge( xlabel='obstacle', **eventedgeattr) >> sys
diag