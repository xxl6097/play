@echo off
git add . -A

set/p option=�������ύ��־:
git commit -m "%option%"

git push
@pause