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
package org.icescrum.plugins.entryPoints
import grails.util.Environment
import org.slf4j.LoggerFactory
import grails.util.GrailsNameUtils

class EntryPointsDeclarationsFactory {

    static private final log = LoggerFactory.getLogger(EntryPointsDeclarationsFactory.name)
    
    static Map<String,Closure> getEntriesDeclarations(grailsApplication, pluginManager, String environment = Environment.current.name) {
        
        if (log.debugEnabled) {
            log.debug("entries config order: ${grailsApplication.entryPointsClasses*.clazz*.name}")
        }
        
        def entryPointsDeclarations = [:]
        
        grailsApplication.entryPointsClasses.collect {
            if (log.debugEnabled) {    
                log.debug("consuming entries config from $it.clazz.name")
            }

            def config = new ConfigSlurper(environment).parse(it.clazz)
            def loadable = config.pluginName ? pluginManager.getUserPlugins().find{ it.name == config.pluginName && it.isEnabled() } : true
            if (loadable){
                def entries = config.entryPoints
                if (entries instanceof Closure) {
                    entryPointsDeclarations[config.pluginName ? GrailsNameUtils.getScriptName(config.pluginName) : it.clazz.name] = entries
                } else {
                    if (entries instanceof ConfigObject) {
                        log.warn("entries artefact $it.clazz.name does not define any entry")
                    } else {
                        log.warn("entries artefact $it.clazz.name mapper element is not a Closure")
                    }
                }
            }else {
                log.warn("entries artefact $it.clazz.name mapper element is not loadable")
            }
        }
        entryPointsDeclarations = entryPointsDeclarations.findAll { it != null}
        entryPointsDeclarations
    }
}