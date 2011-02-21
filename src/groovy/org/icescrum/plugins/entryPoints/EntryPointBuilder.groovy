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