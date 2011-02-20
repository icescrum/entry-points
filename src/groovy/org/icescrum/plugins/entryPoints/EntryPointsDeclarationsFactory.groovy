package org.icescrum.plugins.entryPoints
/*
  Copyright 2011 Vincent BARRIER (barrier.vincent@gmail.com)

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
import grails.util.Environment
import org.slf4j.LoggerFactory

class EntryPointsDeclarationsFactory {

    static private final log = LoggerFactory.getLogger(EntryPointsDeclarationsFactory.name)
    
    static Map<String,Closure> getEntriesDeclarations(grailsApplication, String environment = Environment.current.name) {
        
        def slurper = new ConfigSlurper(environment)

        if (log.debugEnabled) {
            log.debug("entries config order: ${grailsApplication.entryPointsClasses*.clazz*.name}")
        }
        
        def entryPointsDeclarations = [:]
        
        grailsApplication.entryPointsClasses.collect {
            if (log.debugEnabled) {    
                log.debug("consuming entries config from $it.clazz.name")
            }
            
            def entries = slurper.parse(it.clazz).entryPoints
            if (entries instanceof Closure) {
                entryPointsDeclarations[it.clazz.name] = entries
            } else {
                if (entries instanceof ConfigObject) {
                    log.warn("entries artefact $it.clazz.name does not define any entry")
                } else {
                    log.warn("entries artefact $it.clazz.name mapper element is not a Closure")
                }
            }
        }
        entryPointsDeclarations = entryPointsDeclarations.findAll { it != null}
        entryPointsDeclarations
    }
}