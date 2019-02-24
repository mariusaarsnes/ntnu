sour# sec

## Get started
It's recommended to have a look at: https://www.djangoproject.com/start/

Basic tutorial that walks trough what the different files does.
https://docs.djangoproject.com/en/2.0/intro/tutorial01/

Create a virtualenv https://docs.python-guide.org/dev/virtualenvs/


## Local setup

### Installation with examples for ubuntu. Windows and OSX is mostly the same

Fork the project and clone it to your machine.

#### Setup and activation of virtualenv (env that prevents python packages from being installed globaly on the machine)

`pip install virtualenv`

`virtualenv -p python3 env`

`source env/bin/activate`


#### Install python requirements

`pip install -r requirements.txt`


#### Migrate database

`python sec/manage.py migrate`


#### Create superuser

Create a local admin user by entering the following command:

`python sec/manage.py createsuperuser`

Only username and password is required


#### Start the app

`python sec/manage.py runserver`


#### Add initial data

Add initial data go to the url the app is running on localy after `runserver` and add `/admin` to the url.

Add some categories and you should be all set.

or by entering

`python sec/manage.py loaddata seed.json`
