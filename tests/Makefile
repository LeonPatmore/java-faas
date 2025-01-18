install:
	pipenv install --dev

run:
	echo ${CIRCLE_SHA1}
	echo ${FAAS_IMAGE_NAME}
	pipenv run pytest

lint:
	pipenv run flake8 --max-line-length 120
