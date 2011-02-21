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
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class EntryPointsBuilder implements GroovyInterceptable {

    private _entries
    private _entryPointBuilder
    private _data = [points:[]]

    private final log = LoggerFactory.getLogger(this.class.name)

    EntryPointsBuilder(ConcurrentHashMap entries) {
        _entries = entries
        _entryPointBuilder = new EntryPointBuilder(_data)
    }

    def invokeMethod(String name, args) {
        if (args.size() == 1 && args[0] instanceof Closure) {

            def entryPointDefinition = args[0]
            entryPointDefinition.delegate = _entryPointBuilder
            entryPointDefinition.resolveStrategy = Closure.DELEGATE_FIRST
            entryPointDefinition()

            _data.points?.each{ pointId ->
                if (!_entries[pointId]){
                    _entries[pointId] = []
                }
                def entry
                if (_data.controller){
                    entry = [action:_data.action?:'index',controller:_data.controller]
                    _entries[pointId] << entry
                    if (log.debugEnabled)
                        log.debug("Defined new entry for point '$pointId' -> '$entry'")
                }else{
                    if (log.debugEnabled)
                        log.debug("Entry point must have view OR action AND controller")
                }
            }

            // clear for next
            _data.clear()
            _data.points = []
        }
    }

}