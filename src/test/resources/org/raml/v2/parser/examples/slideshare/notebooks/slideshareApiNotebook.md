---
site: https://anypoint.mulesoft.com/apiplatform/popular/admin/#/dashboard/apis/7697/versions/7829/portal/pages/6418/preview
apiNotebookVersion: 1.1.66
title: Slideshare API Notebook
---

```javascript

load('https://github.com/chaijs/chai/releases/download/1.9.0/chai.js')

```



See http://chaijs.com/guide/styles/ for assertion styles



```javascript

assert = chai.assert

```

```javascript

load('https://caligatio.github.io/jsSHA/sha.js')

```

```javascript

API_KEY = prompt("Please, enter your API Key.")

API_SECRET = prompt("Please, enter your API Secret.")

USERNAME = prompt("Please, enter your username.")

PASSWORD = prompt("Please, enter your password.")

```



Some methods which retrieve data related with a particular Slideshare user, require a _username_for_ parameter. Here we set value for this param. Note that you may redefine it as you like.



```javascript

USERNAME_FOR = "API_Notebook_Test_Account"

```



Helper method which generates authentication parameters.



```javascript

function generateAuthenticationParams(){

  var date = new Date()

  var millis = date.getTime()

  var seconds = Math.round(millis/1000)

  

  var hashObj = new jsSHA( API_SECRET + seconds, "TEXT");

  var hash = hashObj.getHash( "SHA-1", "HEX", 1 )

  

  var result = new Object()

  result.timestamp = seconds

  result.hash = hash

  return result

}

```

```javascript

authParams = generateAuthenticationParams()

```

```javascript

// Read about the Slideshare REST API at https://anypoint.mulesoft.com/apiplatform/popular/admin/#/dashboard/apis/7697/versions/7829/contracts

API.createClient('client', '/apiplatform/repository/public/organizations/30/apis/7697/versions/7829/definition');

```



Returns user slideshows



```javascript

get_slideshows_by_userResponse = client.get_slideshows_by_user.get({

  username_for: USERNAME_FOR,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash

})

```

```javascript

assert.equal( get_slideshows_by_userResponse.status, 200 )

```

```javascript

slideshowId = 0

{

  var body = get_slideshows_by_userResponse.body

  var ind1 = body.indexOf("")

  if(ind1>=0){

    ind1 += "".length

    var ind2 = body.indexOf("",ind1)

    var shId = Number.parseInt(body.substring(ind1,ind2)) 

    slideshowId = shId

  }

}

```



Returns slideshow object



```javascript

get_slideshowResponse = client.get_slideshow.get({

  slideshow_id: slideshowId,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash

})

```

```javascript

assert.equal( get_slideshowResponse.status, 200 )

```



Returns slideshows that contain the specified tag



```javascript

get_slideshows_by_tagResponse = client.get_slideshows_by_tag.get({

  tag: "test",

  username_for: USERNAME_FOR,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash

})

```

```javascript

assert.equal( get_slideshows_by_tagResponse.status, 200 )

```



Returns slideshows that are a part of the specified group



**Attention** method not supported



```javascript

// get_slideshows_by_groupResponse = client.get_slideshows_by_group.get({

//   group_name: "Altimeter",

//   api_key: API_KEY,  

//   ts: authParams.timestamp, 

//   hash: authParams.hash

// })

```

```javascript

//assert.equal( get_slideshows_by_groupResponse.status, 200 )

```



Returns user favorites



```javascript

get_user_favoritesResponse = client.get_user_favorites.get({  

  username_for: USERNAME_FOR,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash

})

```

```javascript

assert.equal( get_user_favoritesResponse.status, 200 )

```



Returns slideshows according to the search criteria



```javascript

search_slideshowsResponse = client.search_slideshows.get({

  q: "business",

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash

})

```

```javascript

assert.equal( search_slideshowsResponse.status, 200 )

```



Corrects a slideshow



```javascript

edit_slideshowResponse = client.edit_slideshow.get({

  slideshow_id: slideshowId,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash,

  username: USERNAME,

  password: PASSWORD

})

```

```javascript

assert.equal( edit_slideshowResponse.status, 200 )

```



Upload a slideshow



**Attention** method not supported



```javascript

// upload_slideshowResponse = client.upload_slideshow.get({

//   upload_url: "https://dl.boxcloud.com/bc/4/794ae8510fcd3ed49a055461e37ad394/UxzP28EHPXCXC28QYSNsJCr7UDwdV6kDVHCKT56frIQWfylsD_XPFinw00cyKhkpaNijGnt7Qm8VpBJmiI7Umywc5yWLYsAgc7yIJ7afNqwm5Bc152ITJOO4O1BBv95vGpQM-w-HvztlNBHPjzeIEoVn__blS2tmTn_m9AzAidP93p-l7wZBY1WEo8pTIryxsEkmRQcyvvHq5VUd7CQGZC-hS65Gh9H9HviBkrXaiiFotI78YqvuWxd_3Ztt3Py_7DI_oLFwHTcW-6MJtTF9FfpM65lYA01kpxk84wp7D0CFhlLKYyDmjKq8h6NGBsPaoeaLf0nBawFMk0K2lR1DDINxk7IDsB14gSR403wjugNkvcWZSewj8s3w-Sp9Px6LiTpFXyiukZZs9eFP3IzUdwcWRvi-Abx7FocZfb5hyRmDx0bR3cEKVpRZBt48HejXnJdlzECmdPV5Ich7M3P_0Vuc1drWzEov3isjGjeBuP0NZFxwp-Xiao8DLtECR0O8aR12AOuU3LsLHRktzz3kn8QZqIEiaG9RoQBSZMiVnDlZ-ZgmYPeZz0zaFswPxPFk8m6Ken8ye6fHfKdVqvDz603jefh2cKMyVLW2aZxdIIKhIkmaMmesnxBD0ojajydpvF24sYB9wKYf-Qdb_zcAdOKa881Rite9o5PEH933MjpqLzkDUIh3oDijfIvaDS2lpSCLgBOUG9nRqI-uKloUu4-vF3FjzCigWbez05aRKOKHtyw9tZgqHL5_CCyQXqziCJsVzUyY6wHhgtBfV5OUOHNkhV3RamFtcx7Msr0UpJkyzTeokFdZRkEDbijRFIUdb6ly1s2oZss_ddAOefaR/",

//   api_key: API_KEY,  

//   ts: authParams.timestamp, 

//   hash: authParams.hash,

//   username: USERNAME,

//   password: PASSWORD

// })

```

```javascript

//upload_slideshowResponse.body

```

```javascript

//assert.equal( upload_slideshowResponse.status, 200 )

```



Returns user contacts



```javascript

get_user_contactsResponse = client.get_user_contacts.get({

  username_for: USERNAME_FOR,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash

})

```

```javascript

assert.equal( get_user_contactsResponse.status, 200 )

```



Returns user groups



**Attention** method not supported



```javascript

// get_user_groupsResponse = client.get_user_groups.get({

//   username_for: USERNAME_FOR,

//   api_key: API_KEY,  

//   ts: authParams.timestamp, 

//   hash: authParams.hash,

//   username: USERNAME,

//   password: PASSWORD

// })

```

```javascript

//assert.equal( get_user_groupsResponse.status, 200 )

```



Returns user tags



```javascript

get_user_tagsResponse = client.get_user_tags.get({

  username_for: USERNAME_FOR,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash,

  username: USERNAME,

  password: PASSWORD

})

```

```javascript

assert.equal( get_user_tagsResponse.status, 200 )

```



Check user favorites



```javascript

check_favoriteResponse = client.check_favorite.get({

  slideshow_id: 34134362,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash,

  username: USERNAME,

  password: PASSWORD

})

```

```javascript

assert.equal( check_favoriteResponse.status, 200 )

```



Favorites slideshow (identified by slideshow_id)



```javascript

add_favoriteResponse = client.add_favorite.get({

  slideshow_id: 34134362,

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash,

  username: USERNAME,

  password: PASSWORD

})

```

```javascript

assert.equal( add_favoriteResponse.status, 200 )

```



Get user campaign leads



```javascript

get_user_campaign_leadsResponse = client.get_user_campaign_leads.get({

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash,

  username: USERNAME,

  password: PASSWORD

})

```

```javascript

assert.equal( get_user_campaign_leadsResponse.status, 200 )

```



Get user campaigns



```javascript

get_user_campaignsResponse = client.get_user_campaigns.get({

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash,

  username: USERNAME,

  password: PASSWORD

})

```

```javascript

assert.equal( get_user_campaignsResponse.status, 200 )

```



Get user leads



```javascript

get_user_leadsResponse = client.get_user_leads.get({

  api_key: API_KEY,  

  ts: authParams.timestamp, 

  hash: authParams.hash,

  username: USERNAME,

  password: PASSWORD

})

```

```javascript

assert.equal( get_user_leadsResponse.status, 200 )

```



Deletes a slideshow



**Attention**

As we can not now create slideshows via REST, this method is skipped for sake of automation.



```javascript

// delete_slideshowResponse = client.delete_slideshow.get({

//   slideshow_id: slideshowId,

//   api_key: API_KEY,

//   username: USERNAME,

//   password: PASSWORD,  

//   ts: authParams.timestamp, 

//   hash: authParams.hash  

// })

```

```javascript

//assert.equal( delete_slideshowResponse.status, 200 )

```