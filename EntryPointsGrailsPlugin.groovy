/*
  Copyright 2011 Vincent BARRIER

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
import org.icescrum.plugins.entryPoints.artefacts.EntryPointsArtefactHandler

class EntryPointsGrailsPlugin {
    def groupId = 'org.icescrum.plugins'
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def artefacts = [new EntryPointsArtefactHandler()]

    def watchedResources = [
        "file:./grails-app/conf/*EntryPoints.groovy",
        "file:./plugins/*/grails-app/conf/*EntryPoints.groovy",
    ]

    def loadAfter = ['controllers']

    // TODO Fill in these fields
    def author = "Vincent Barrier"
    def authorEmail = "barrier.vincent@gmail.com"
    def title = "Entry points for your grails app (entry points inside gsp)"
    def description = '''
        used in iceScrum -> http://www.icescrum.org
    '''

    // URL to the plugin's documentation
    def documentation = "http://www.icescrum.org/plugin/entry-points"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        applicationContext.entryPointsService.reload()
    }

    def onChange = { event ->
        def type = EntryPointsArtefactHandler.TYPE
        if (application.isArtefactOfType(type, event.source)) {
            log.debug("reloading $event.source.name ($type)")
            def oldClass = application.getArtefact(type, event.source.name)
            application.addArtefact(type, event.source)
            application.getArtefacts(type).each {
                if (it.clazz != event.source && oldClass.clazz.isAssignableFrom(it.clazz)) {
                    def newClass = application.classLoader.reloadClass(it.clazz.name)
                    application.addArtefact(type, newClass)
                }
            }
            event.application.mainContext.entryPointsService.reload()
        }
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}