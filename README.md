# Passera

A simple tool written in Go that allows users to have strong unique
passwords for each website, without the need to store them either
locally or with an online service.

Passera turns any entered text into a strong password up to 64
characters long and copies it to clipboard. Figure out a decent system
for yourself that will allow unique passphases for every website, such
as combining website name/url with a phrase that you would not forget.
To login, fire up Passera and enter the password you chose and your
real password will be copied to clipboard.

#### Turn `githubPasswd123` into `dpu7{Lrby(vQLd8m`

This software is for privacy-aware people that understand the need to
have strong unique passwords for each website, yet don't want to use
any password managing software or services.

Passwords created with Passera are extremely difficult to bruteforce
and impossible to revert back to the original regardless of attacker's
knowledge of the source code. If one of your passwords is compromised
after an attack on you or a web service, all your other passwords are
safe with you.

To make it somewhat more conspicuous, when you start Passera it copies
a random password to clipboard. The real password is then only stored
in clipboard for 10 seconds, before being overwritten by another
random string.

# Usage
The software does one thing and does it good. Fire it up and it will
prompt you for the passphase you chose for a particular service you
wish to login. It will produce your password and copy it to clipboard,
counting down from 10 seconds before overwriting it with a fake one.
```
$ passera
>> 
Copied to clipboard
Clearing in 9
```

Specify password length (defaults to 16):

```
$ passera -l 64
```

Disable copying fake passwords to clipboard before and after the real password:

```
$ passera -d 
```

Disable copying passwords to clipboard altogether, only show the real password on the screen (be sure to clean up your bash history):

```
$ passera -s
```

Specify time delay (in seconds) before clipboard is replaced with a phony password (keep in mind it may be overwritten by other programs): 
```
$ passera -t 60
```
