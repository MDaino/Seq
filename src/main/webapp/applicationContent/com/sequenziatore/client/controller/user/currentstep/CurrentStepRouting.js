/*
 * Copyright [2014] [DeSQ]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 * Name : CurrentStepRouting.js
 * Module : CurrentStepRouting
 * Location : /applicationContent/com/sequenziatore/client/controller/user
 *
 * History :
 *
 * Version         Date           Programmer
 * ======================================================
 * 0.0.1           2014-04-11     Sartoretto Massimiliano
 * ------------------------------------------------------
 * Codifica Modulo
 * ======================================================
 */

var application = angular.module('CurrentStepRouting',['ngRoute','CtrlCurrentStep']);

application.config(['$routeProvider', function($routeProvider){
    $routeProvider.when('/currentstep',
        {
            templateUrl: 'applicationContent/com/sequenziatore/client/view/user/currentstep.html'
        });
}]);
