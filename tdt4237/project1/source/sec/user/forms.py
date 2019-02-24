from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from projects.models import ProjectCategory


class SignUpForm(UserCreationForm):
    company = forms.CharField(max_length=30, required=False, help_text='Here you can add your company.')
    phone_number = forms.CharField(max_length=50)

    street_address = forms.CharField(max_length=50)
    city = forms.CharField(max_length=50)
    state = forms.CharField(max_length=50)
    postal_code = forms.CharField(max_length=50)
    country = forms.CharField(max_length=50)

    email = forms.EmailField(max_length=254, help_text='Inform a valid email address.')
    categories = forms.ModelMultipleChoiceField(queryset=ProjectCategory.objects.all(),
                                                help_text='Hold down "Control", or "Command" on a Mac, to select more than one.',
                                                required=False)

    class Meta:
        model = User
        fields = ('username', 'first_name', 'last_name', 'categories', 'company', 'email', 'password1', 'password2',
                  'phone_number', 'street_address', 'city', 'state', 'postal_code', 'country')


class LoginForm(forms.Form):
    username = forms.CharField(required=True)
    password = forms.CharField(required=True, widget=forms.TextInput(attrs={"type": "password"}))
