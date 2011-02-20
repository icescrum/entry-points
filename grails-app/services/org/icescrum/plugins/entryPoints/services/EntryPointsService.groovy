package org.icescrum.plugins.entryPoints.services
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
import org.springframework.beans.factory.InitializingBean
import java.util.concurrent.ConcurrentHashMap
import org.icescrum.plugins.entryPoints.EntryPointsDeclarationsFactory
import org.icescrum.plugins.entryPoints.EntryPointsBuilder

class EntryPointsService implements InitializingBean {

    static transactional = false

    def grailsApplication
    def servletContext
    def entriesByPointId

    void afterPropertiesSet() {
        if (!servletContext) {
            servletContext = grailsApplication.mainContext.servletContext
        }
    }

    def getEntries(def pointId){
        entriesByPointId[pointId]
    }

    private loadEntries() {
        if (log.infoEnabled) {
            log.info "Loading entries declarations..."
        }

        entriesByPointId = new ConcurrentHashMap()
        def declarations = EntryPointsDeclarationsFactory.getEntriesDeclarations(grailsApplication)
        def builder = new EntryPointsBuilder(entriesByPointId)

        declarations.each { sourceClassName, dsl ->
            if (log.debugEnabled) {
                log.debug("evaluating entries from $sourceClassName")
            }
            dsl.delegate = builder
            dsl.resolveStrategy = Closure.DELEGATE_FIRST
            dsl()
        }
    }

    def reload() {
        log.info("Reloading entries points")
        loadEntries()
    }
}
