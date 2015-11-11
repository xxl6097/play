@echo off
git add . -A

set/p option=请输入提交日志:
git commit -m "%option%"

git push
@pause