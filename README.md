# VDAB M2X Nodes
### Overview 
AT&T's M2X provides time series data storage for the Internet of Things. The M2X service demonstrates how
a node can be constructed to store VDAB data directly into M2X. 

| | |
|  --- |  :---: |
| Application Page    | NA |
| Demo Web Link   |  NA |

### Features
<ul>
<li>Allows storage of VDAB event data in AT&T's M2X data cloud.
<li>The <i>M2XService</i> pushes VDAB event data into a selected M2X Stream.
</ul>

### Loading the the Package
The current or standard version can be loaded directly using the VDAB Android Client following the directions
for [Adding Packages](https://vdabtec.com/vdab/docs/VDABGUIDE_AddingPackages.pdf) 
and selecting the <i>M2XNodes</i> package.
 
A custom version can be built using Gradle following the direction below.

* Clone or Download this project from Github.
* Open a command windows from the <i>M2XNodes</i> directory.
* Build using Gradle: <pre>      gradle vdabPackage</pre>

This builds a package zip file which contains the components that need to be deployed. These can be deployed by 
manually unzipping these files as detailed in the [Server Updates](https://vdabtec.com/vdab/docs/VDABGUIDE_ServerUpdates.pdf) 
 documentation.

### Known Issues as of 24 Oct  2018

* This demonstrates how to integrate with M2X and has limited capabilities.


