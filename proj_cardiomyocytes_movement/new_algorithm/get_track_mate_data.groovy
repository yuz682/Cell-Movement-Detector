#@ ImagePlus imp
#@ double (label = "Quality threshold") threshold

File outputFolderFile = new File("./Working_Directory.txt")
String outputFolder = outputFolderFile.text
outputFolder = outputFolder + "medium_products/"
File radiusFile = new File(outputFolder + "approx_roi_radius.txt")
String fileContent = radiusFile.text
double radius = fileContent.toDouble()
filename = "raw_movement_data"
double linkingMax = 5.0
frameGap = 2
double closingMax = 15

import javax.swing.*
import fiji.plugin.trackmate.Model
import fiji.plugin.trackmate.Settings
import fiji.plugin.trackmate.TrackMate
import fiji.plugin.trackmate.detection.LogDetectorFactory
import fiji.plugin.trackmate.tracking.LAPUtils
import fiji.plugin.trackmate.tracking.sparselap.SparseLAPTrackerFactory
import fiji.plugin.trackmate.action.ExportTracksToXML
//import fiji.plugin.trackmate.action.ExportAllSpotsStatsAction
import fiji.plugin.trackmate.action.ExportStatsToIJAction

// Pop up an input box to user
def prompt = {
  JFrame jframe = new JFrame()
  String answer = JOptionPane.showInputDialog(jframe, it)
  jframe.dispose()
  answer
}

// Swap Z and T dimensions if T=1
dims = imp.getDimensions() // default order: XYCZT
if (dims[4] == 1) {
	imp.setDimensions( dims[2,4,3] )
}

// Setup settings for TrackMate
settings = new Settings()
settings.setFrom(imp)
settings.dt = 0.05

settings.detectorFactory = new LogDetectorFactory()
settings.detectorSettings = settings.detectorFactory.getDefaultSettings()
println settings.detectorSettings

settings.detectorSettings['RADIUS'] = radius
settings.detectorSettings['THRESHOLD'] = threshold
println settings.detectorSettings

settings.trackerFactory = new SparseLAPTrackerFactory()
settings.trackerSettings = LAPUtils.getDefaultLAPSettingsMap()

settings.trackerSettings['MAX_FRAME_GAP'] = frameGap
settings.trackerSettings['LINKING_MAX_DISTANCE'] = linkingMax
settings.trackerSettings['GAP_CLOSING_MAX_DISTANCE'] = closingMax

// Run TrackMate and store data into Model
model = new Model()
trackmate = new TrackMate(model, settings)

println trackmate.checkInput()
println trackmate.process()
println trackmate.getErrorMessage()
println model.getSpots().getNSpots(true)
println model.getTrackModel().nTracks(true)


// Save tracks as XML
if (!filename.endsWith(".xml")) {
	filename += ".xml"
}
outFile = new File(outputFolder, filename)
ExportTracksToXML.export(model, settings, outFile)

File qtf = new File(outputFolder + "quality_threshold.txt")
qtf.write(threshold.toString())