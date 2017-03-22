/*
* Copyright (c) 2011 Vincent Barrier.
*
*
* entry-points is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License.
*
* entry-points is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with iceScrum.  If not, see <http://www.gnu.org/licenses/>.
*
*/
import org.icescrum.plugins.entryPoints.artefacts.EntryPointsArtefactHandler
import org.icescrum.plugins.entryPoints.services.EntryPointsService

class EntryPointsGrailsPlugin {
    def groupId = 'org.icescrum'
    def version = "1.2"
    def grailsVersion = "2.5 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def artefacts = [new EntryPointsArtefactHandler()]

    def watchedResources = [
        "file:./grails-app/conf/*EntryPoints.groovy",
        "file:./plugins/*/grails-app/conf/*EntryPoints.groovy",
    ]

    def loadAfter = ['controllers']

    def author = "Vincent Barrier"
    def authorEmail = "barrier.vincent@gmail.com"
    def title = "Entry points for your grails app (entry points inside gsp & controllers)"
    def description = '''
        used in iceScrum -> http://www.icescrum.org
    '''

    def documentation = "http://www.icescrum.org/plugin/entry-points"

    def doWithDynamicMethods = { ctx ->
        EntryPointsService service = ctx.getBean('entryPointsService')
        application.controllerClasses.each {
            addEntryPointsMethods(it, service)
        }
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
        event.application.mainContext.entryPointsService.reload()
    }
    
    private addEntryPointsMethods(it, service) {
        it.clazz.metaClass {
            entryPoints { String ref, Map model = null ->
                assert ref
                if (model instanceof Map) {
                    model.each { request."${it.key}" = it.value }
                } else {
                    request.model = model
                }
                service.getEntriesToChain(ref)?.each {
                    forward(action: it.form?.action ?: it.action, controller: it.form?.controller ?: it.controller)
                }
            }
        }
    }
}