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
    def webInvocationPrivilegeEvaluator

    def hook = { attrs ->
        assert attrs.id

        if (!attrs.model){
            attrs.model = [:]
        }

        attrs.model.requestParams = params

        entryPointsService.getEntries(attrs.id)?.each{ entry ->
            def controller = grailsApplication.controllerClasses.find{ it.name.toLowerCase() == entry.controller }?.getReferenceInstance()
            controller?."${entry.action}"()
        }
    }

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
               def url = createLink(controller:entry?.controller, action:entry?.action).toString() - request.contextPath
               def access = true
               if(webInvocationPrivilegeEvaluator != null){
                   access = webInvocationPrivilegeEvaluator.isAllowed(org.codehaus.groovy.grails.plugins.springsecurity.SecurityRequestHolder.request.contextPath, url, 'GET', org.springframework.security.core.context.SecurityContextHolder.context?.authentication)
               }
               if (access){
                   out << g.include(action:entry?.action,controller:entry?.controller,params:attrs.model)
               }
           }
        }
    }
}
