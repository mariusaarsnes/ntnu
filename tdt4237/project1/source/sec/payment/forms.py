from django import forms


class PaymentForm(forms.Form):
    cardnumber = forms.CharField(max_length=20, label="Card Number", initial="")
    expirymonth = forms.CharField(max_length=2, label="Expiry Month", initial="")
    expiryyear = forms.CharField(max_length=2, label="Expiry Year", initial="")
    cvc = forms.CharField(max_length=4, label="CVC", initial="")
