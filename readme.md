#Tortuga
Задание к лабораторной работе:

 создать клиент-серверное приложение «Портовая диспетчерская система»;

 реализация GUI клиента должна использовать одну из библиотек:

Swing/SWT/JavaFX;

 у каждого порта есть склад и причалы;

 корабли швартуются к причалам, после чего могут загрузиться-разгрузиться со

склада порта;

 у каждого причала может швартоваться только один корабль, остальные

корабли, желающий зайти в порт, должны ждать в очереди;

 все действия кораблей выводятся для визуального отслеживания событий;

 раз в пять секунд диспетчерская система выводит информацию о состоянии

порта в файл (информацию о количестве товаров на складе, кораблях у причалах и в

очереди);

 *при швартовке корабль указывает порту её длительность, система следит за

продолжительностью швартовки и уведомляет отдельными сообщениями о превышении;

 *наличие приоритетов у кораблей, важности и срочности грузов, прошлой

статистики корабля (наличие нарушение и т.д.).