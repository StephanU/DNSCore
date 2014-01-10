package daweb3
/**
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
class PackageController {

   	def index() {
			redirect(action: "show", params: params)
		}
	
	   def show() {
		   def packageInstance = Package.get(params.id)
		   if (!packageInstance) {
			   flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Package'), params.id])
			   redirect(action: "list")
			   return
		   }
		   [packageInstance: packageInstance]
	   }
   
	   }
