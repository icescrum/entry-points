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
class EntryPointBuilder {
    
    private final _data

    EntryPointBuilder(def data) {
        _data = data    
    }
        
    void action(String value) {
        _data.action = value
    }

    void controller(String value) {
        _data.controller = value
    }

    void points(List _points) {
        _data.points.addAll(_points)
    }

    void points(String[] _points) {
        _data.points.addAll(_points.toList())
    }

    void points(String _points) {
        points(_points.split(',')*.trim())
    }
    
    def missingMethod(String name, args) {
        throw new RuntimeException("Sorry - flavours are not yet supported by the builder!")
    }
}