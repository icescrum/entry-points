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

import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

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
