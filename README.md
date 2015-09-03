#athento-nuxeo-rm

En este repositorio están contenidos los plugins "nuxeo-rm" y "nuxeo-dm". Estos dos plugins están basados en lo que se conoce como "records management" (RM) que es una disciplina encargada del control eficiente y sistemático de la creación, recepción, mantenimiento, y uso de activos de información a los que se le denominan "records". El objetivo principal de esta disciplina es ayudar a mantener accesible la documentación necesaria tanto para las operaciones comerciales como las auditorías de cumplimiento.

Ambos plugins (funcionan en conjunto, es decir, depende uno del otro por lo que no funcionan de forma independiente) permiten la creación de los tipos documentales "Cuadros de Clasificación" (CdC) y "Calendarios de conservación". El primer tipo documental mencionado es uno de los instrumentos principales de organización de documentos, recomendado por la ISO 15489. El CdC representa de forma jerárquica las actividades de la organización de la que dependen, estructurándolas en clases y grupos según las funciones marco, funciones, actividades y acciones. Normalmente, las acciones se corresponden con las series documentales.

La identificación y valoración de los documentos es un elemento indispensable para que sea efectivo el acceso a la documentación activa y la reducción de documentos innecesarios. Esto se hace posible gracias al tipo documental mencionado anteriormente "Calendarios de conservación" el cual establece los plazos precisos de conservación y remisión de cada serie documental en los archivos de gestión. 

#Instalación

Como son dos plugins, es necesario compilar dos archivos "pom.xml" utilizando Maven y desplegar posteriormente ambos plugins. Para el plugin "nuxeo-dm", hay que hacer uso de la siguiente secuencia de comandos:

	cd nuxeo-dm
	mvn clean install -Dmaven.test.skip=true
	cp target/nuxeo-dm-*.jar $NUXEO_HOME/nxserver/plugins

Para el plugin "nuxeo-rm se hace uso de la siguiente secuencia de comandos:

	cd nuxeo-rm
	mvn clean install
	cp target/nuxeo-rm*.jar $NUXEO_HOME/nxserver/plugins

Por último, una vez cargados los plugins, es necesario reiniciar el servidor de Nuxeo y ya se puede disfrutar de ambos plugins.




