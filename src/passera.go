package main

import (
	"github.com/howeyc/gopass"
	"github.com/atotto/clipboard"
	"fmt"
	"crypto/sha512"
	"encoding/base64"
	"encoding/hex"
	"strings"
	"regexp"
	"strconv"
	"time"
	"os"
	"flag"
)

func hash(pw string, len int, chars bool) string {

	salt := passwd(pw, 64, chars)
	runs := 0

	for i, _ := range salt {
		runs = runs + int(salt[i])
	}
	runs += len
	for i:=0; i<runs; i++ {
		hasher := sha512.New()
		hasher.Write([]byte(salt+pw))
		pw = hex.EncodeToString(hasher.Sum(nil))

	}

	return pw
}

func passwd(str string, len int, chars bool ) string {

	hasher := sha512.New()
	hasher.Write([]byte(str))
	hash := base64.StdEncoding.EncodeToString(hasher.Sum(nil))

	replacer := sha512.New()
	replacer.Write([]byte(hash))
	replace := base64.StdEncoding.EncodeToString(replacer.Sum(nil))

	passwd := hash[0:len]

	if chars == true {
		replaceables := [21]string{ "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "+", "=", "]", "[", "{", "}", "?", "<", ">" }
		r, _ := regexp.Compile("[0-9]")
		nums := r.FindAllString(hash, 1)

		num := 0
		var curnum int
		for _, c := range replace[0:len / 2] {
			curnum, _ = strconv.Atoi(nums[0])
			num = num+curnum
			if num > 20 {
				num = num-20
			}
			passwd = strings.Replace(passwd, string(c), replaceables[num], 1)
		}
	}

	return passwd
}

func phony(length int) {
	phony := passwd(strconv.FormatInt(time.Now().UnixNano(), 10), length, true)
	clipboard.WriteAll(phony)
}

func main() {

	var secs int
	var length int

	flag.IntVar(&length, "l", 16, "password length")
	flag.IntVar(&secs, "t", 10, "seconds to keep password in clipboard")
	nofo := flag.Bool("d", false, "do not copy phony passwords before and after the real one ")
	show := flag.Bool("s", false, "only show the password on the screen")
	chars := flag.Bool("c", true, "use special characters in the password (!, @, #, etc.)")
	vrf := flag.Bool("v", false, "ask for password twice to verify its correctness when setting a password for a website")
	flag.Parse()

	if length > 64 {
		fmt.Println("Maximum password length is 64 and will be set as such")
		length = 64
	} else if length < 4 {
		fmt.Println("Seriously? Setting password length to 4")
		length = 4
	}

	if *nofo != true && *show != true { phony(length) }

	fmt.Printf(">> ")
	phrase := gopass.GetPasswd()

	if *vrf == true {
		fmt.Printf(">>> ")
		phrase2 := gopass.GetPasswd()
		if string(phrase) != string(phrase2) {
			fmt.Println("Passwords did not match")
			return
		}

	}

	pw := passwd( hash( string(phrase), length, *chars ), length, *chars )

	if *show == true {

		os.Stdout.Write([]byte(pw+"\r"))
		time.Sleep(time.Duration(secs) * 1000000000)
		os.Stdout.Write([]byte("                                                                \r\n"))

	} else {
		clipboard.WriteAll(pw)
		fmt.Println("Copied to clipboard")
		for z := secs; z > 0; z-- {
			os.Stdout.Write([]byte("Clearing in " + strconv.Itoa(z) + " \r"))
			time.Sleep(1000000000)
		}
		os.Stdout.Write( []byte("\n") )
	}

	if *nofo != true && *show != true { phony(length) }
}
