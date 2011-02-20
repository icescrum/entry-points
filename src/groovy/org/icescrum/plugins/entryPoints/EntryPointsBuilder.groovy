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