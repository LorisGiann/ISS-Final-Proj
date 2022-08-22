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
with Diagram('demo0Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxserver', graph_attr=nodeattr):
          ledcommander=Custom('ledcommander','./qakicons/symActorSmall.png')
          sonardisplay=Custom('sonardisplay','./qakicons/symActorSmall.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          sonar=Custom('sonar','./qakicons/symActorSmall.png')
          led=Custom('led','./qakicons/symActorSmall.png')
          sonarsimulator=Custom('sonarsimulator(coded)','./qakicons/codedQActor.png')
          sonardatasource=Custom('sonardatasource(coded)','./qakicons/codedQActor.png')
          datacleaner=Custom('datacleaner(coded)','./qakicons/codedQActor.png')
          robotalarmcontrol=Custom('robotalarmcontrol(coded)','./qakicons/codedQActor.png')
          ledalarmcontrol=Custom('ledalarmcontrol(coded)','./qakicons/codedQActor.png')
     ledcommander >> Edge( xlabel='update_led', **eventedgeattr) >> sys
     sys >> Edge(color='red', style='dashed', xlabel='sonardata') >> sonardisplay
     sonar >> Edge(color='blue', style='solid', xlabel='sonaractivate') >> sonarsimulator
     sonar >> Edge(color='blue', style='solid', xlabel='sonaractivate') >> sonardatasource
     sys >> Edge(color='red', style='dashed', xlabel='alarmsonar') >> sonar
     sonar >> Edge(color='blue', style='solid', xlabel='sonardeactivate') >> sonarsimulator
     sonar >> Edge(color='blue', style='solid', xlabel='sonardeactivate') >> sonardatasource
     sonar >> Edge( xlabel='sonardata', **eventedgeattr) >> sys
     sys >> Edge(color='red', style='dashed', xlabel='update_led') >> led
diag
