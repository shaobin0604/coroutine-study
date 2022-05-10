import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Post(val title: String, val content: String)

fun verifyKeys(jsonObject: JSONObject, vararg keys: String) {
    for (key in keys) {
        if (!jsonObject.has(key)) {
            throw RuntimeException("$jsonObject missing key: $key")
        }
    }
}

fun checkName(jsonObject: JSONObject) {
    if (jsonObject.getString("name") != jsonObject.getString("full_name")) {
        throw RuntimeException("$jsonObject name not same")
    }
}

fun convertCask(jsonObject: JSONObject): Post {
    val name = jsonObject.getJSONArray("name")[0]
    val token = jsonObject.getString("token")
    val desc = jsonObject["desc"].toString()
    val version = jsonObject["version"]
    val homepage = jsonObject["homepage"]
    val title = "Install $name on MacOS with Brew – Monterey, Big Sur, Mojave, Catalina, High Sierra, Capitan"
    val content = """
    ---
    title: "$title"
    categories:
    - Cask
    date: 2022-02-18T00:16:06+08:00
    draft: false
    ---

    This article explains the steps to install $name on MacOS using homebrew

    - App Name: $name
    - App description: $desc
    - App Version: $version
    - App Website: $homepage

    # Install steps

    1. Open Spotlight search using "**command + space**” button and type "**Terminal**". Then press "**return/enter**" key. This will open terminal.
    2. Run the following command in terminal to install **Homebrew**
       ```
       /bin/bash -c "${'$'}(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
       ```
    3. Now install $name with the following command
       ```
       brew install --cask $token
       ```
    4. $name is ready to use now!
""".trimIndent()
    return Post(token, content)
}

fun convertCore(jsonObject: JSONObject): Post {
    val name = jsonObject.getString("name")
    val title = "Install $name on MacOS with Brew  – Monterey, Big Sur, Mojave, Catalina, High Sierra, Capitan"
    val desc = jsonObject["desc"].toString()
    val content = """
    ---
    title: "$title"
    categories:
    - Core
    date: 2022-02-18T00:16:06+08:00
    draft: false
    ---

    This article explains the steps to install ${jsonObject["name"]} on MacOS using homebrew

    - App Name: $name
    - App description: $desc
    - App Version: ${jsonObject.getJSONObject("versions").getString("stable")}
    - App Website: ${jsonObject["homepage"]}

    # Install steps

    1. Open Spotlight search using "**command + space**” button and type "**Terminal**". Then press "**return/enter**" key. This will open terminal.
    2. Run the following command in terminal to install **Homebrew**
       ```
       /bin/bash -c "${'$'}(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
       ```
    3. Now install $name with the following command
       ```
       brew install $name
       ```
    4. $name is ready to use now!
""".trimIndent()
    return Post(name, content)
}

fun writeToFile(post: Post) {
    val file = File("post", post.title.lowercase().replace("[^a-zA-Z0-9.-]".toRegex(), "-") + ".md")
//    println(file.absolutePath)
//    println(post.content)
    file.writeText(post.content)
}

fun main() {
    mapOf<String, (JSONObject) -> Post>(
//        "cask.json" to ::convertCask,
        "formula.json" to ::convertCore
    ).forEach { entry ->
        {}::class.java.getResourceAsStream(entry.key)?.bufferedReader()?.use { it.readText() }?.let { json ->
            JSONArray(json).let {
                val end = it.length()
//            val end = 1
                for (i in (0 until end)) {
//                val md = convertCore(it.getJSONObject(i))
//                println(md)
//                checkName(it.getJSONObject(i))
//                verifyKeys(it.getJSONObject(i), "name", "desc", "versions", "homepage")
                    val post = entry.value.invoke(it.getJSONObject(i))
                    writeToFile(post)
                }
            }
        }
    }


}

