package org.icescrum.plugins.entryPoints

import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil
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
class EntryPointsTagLib {
    static namespace = 'entry'
    def entryPointsService
    def pluginManager

    def point = { attrs ->
        assert attrs.id

        if (!attrs.model){
            attrs.model = [:]
        }

        attrs.model.requestParams = params

        if (!GrailsUtil.getEnvironment().equals(GrailsApplication.ENV_PRODUCTION)){
            if (grailsApplication.config.grails.entryPoints?.debug || params._showEntryPoints){
                out << """<span class='entry-point' title='[model/params: ${attrs.model*.key?.join(',')}]'>
                            entry-point id: ${attrs.id}
                        </span>"""
            }
        }

        entryPointsService.getEntries(attrs.id)?.each{ entry ->
            if (entry.template){
                    out << g.render(template:entry.template,model:attrs.model,plugin:entry.plugin)
            }else{
                out << g.include(action:entry?.action,controller:entry?.controller,params:attrs.model)
            }
        }
    }
}
