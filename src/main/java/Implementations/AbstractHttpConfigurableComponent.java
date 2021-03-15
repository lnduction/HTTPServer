package Implementations;

import Configs.HttpServerConfig;

abstract class AbstractHttpConfigurableComponent {
     final HttpServerConfig httpServerConfig;

     AbstractHttpConfigurableComponent(HttpServerConfig httpServerConfig) {
         super();
         this.httpServerConfig = httpServerConfig;
     }
}
