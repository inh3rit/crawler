@echo off
::#######
::# windows 部署使用此脚本启动, 另如需隐藏 CMD 窗口,
::# 执行 HideCMD.exe 名称输入 start "NAME" 这里的名称
::#######
start "crawler_slave" java -jar crawler-slave.jar