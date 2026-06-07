package com.example.data

data class Destination(
    val id: String,
    val name: String,
    val region: String,
    val category: String, // "Hill Stations", "Heritage", "Beaches & Coastal", "Nature & Wildlife"
    val summary: String,
    val description: String,
    val bestTimeToVisit: String,
    val attractions: List<String>,
    val localFood: List<String>,
    val insiderTip: String,
    val imageUrl: String,
    val coordinates: String
)

object DestinationsData {
    val categories = listOf("All", "Heritage", "Hill Stations", "Beaches & Coastal", "Nature & Wildlife")

    val list = listOf(
        Destination(
            id = "madurai",
            name = "Madurai",
            region = "Southern Tamil Nadu",
            category = "Heritage",
            summary = "One of the oldest continuously inhabited cities in the world, famous for its magnificent multi-tiered temple complex.",
            description = "Madurai is the cultural soul of Tamil Nadu, built around the glorious Meenakshi Sundareswarar Temple. Walk along streets shaped like lotus petals and breathe in the rich history dating back to the Pandya kings. It's a bustling hub of commerce, spiritual energy, and legendary street food.",
            bestTimeToVisit = "October to March",
            attractions = listOf("Meenakshi Amman Temple", "Thirumalai Nayakkar Mahal", "Alagar Kovil Temple", "Gandhi Memorial Museum"),
            localFood = listOf("Jigarthanda (Chilled sweet rose/almond shake)", "Madurai Bun Parotta", "Kothu Parotta", "Idiyappam with coconut milk"),
            insiderTip = "Visit the Meenakshi Temple either early in the morning (5:00 AM) or after 8:00 PM to attend the night ceremony and witness the rituals in a relatively serene atmosphere.",
            imageUrl = "https://images.unsplash.com/photo-1600100395861-5052d919c5a1?auto=format&fit=crop&w=800&q=80",
            coordinates = "9.9252° N, 78.1198° E"
        ),
        Destination(
            id = "ooty",
            name = "Ooty",
            region = "Western Ghats / Nilgiris",
            category = "Hill Stations",
            summary = "The 'Queen of Hill Stations', cradled in the majestic blue Nilgiri hills with tea gardens and mist-covered valleys.",
            description = "Ooty (Udhagamandalam) is an iconic retreat characterized by colonial heritage, vast tea estates, high-altitude lakes, and dense pine forests. The scenic Nilgiri Mountain Railway toy train (a UNESCO World Heritage site) completes this romantic hillside landscape.",
            bestTimeToVisit = "April to June & September to November",
            attractions = listOf("Nilgiri Mountain Toy Train", "Ooty Botanical Gardens", "Doddabetta Peak", "Pykara Lake & Waterfalls", "Pine Forest"),
            localFood = listOf("Ooty Varkey (crispy baked puff pastry)", "Nilgiri Green Tea", "Homemade dark and mint chocolates", "Fresh organic carrots"),
            insiderTip = "To secure a seat on the heritage Toy Train from Mettupalayam up to Ooty, book your tickets well in advance via the IRCTC portal, especially during peak summer seasons.",
            imageUrl = "https://images.unsplash.com/photo-1590050752117-238cb0612b1b?auto=format&fit=crop&w=800&q=80",
            coordinates = "11.4102° N, 76.6950° E"
        ),
        Destination(
            id = "thanjavur",
            name = "Thanjavur",
            region = "Cauvery Delta Region",
            category = "Heritage",
            summary = "The historic heart of Chola art and architecture, globally renowned for the thousand-year-old Brihadeeswarar Temple.",
            description = "Thanjavur (Tanjore) is the historic rice-bowl region of Tamil Nadu and the birthplace of the Bharatanatyam dance form and Tanjore paintings. The Great Living Chola Temples reflect an era of unparalleled artistic devotion and engineering genius.",
            bestTimeToVisit = "November to March",
            attractions = listOf("Brihadeeswarar 'Big' Temple", "Thanjavur Royal Palace Complex", "Saraswathi Mahal Library", "Art Gallery & Bronze Palace"),
            localFood = listOf("Thanjavur Royal Thali (served on banana leaf)", "Kadappa (traditional potato gravy for idli)", "Ashoka Halwa (tasty lentil dessert)"),
            insiderTip = "Observe the shadow of the main Vimanam (temple tower) of the Big Temple; it is designed such that it never falls on the ground at noon, demonstrating outstanding architectural planning.",
            imageUrl = "https://images.unsplash.com/photo-1621360841013-c7683c659ec6?auto=format&fit=crop&w=800&q=80",
            coordinates = "10.7870° N, 79.1378° E"
        ),
        Destination(
            id = "mahabalipuram",
            name = "Mahabalipuram",
            region = "East Coast Road (ECR)",
            category = "Beaches & Coastal",
            summary = "A mesmerizing shore town containing UNESCO World Heritage stone-cut monoliths and coastal temples from the Pallava dynasty.",
            description = "Mamallapuram is an open-air museum where ancient rock-cut cave temples stand strong against the crashing waves of the Bay of Bengal. Established by Pallava kings, the site is globally admired for its exquisite carvings, coastal vibes, and vibrant local surfing culture.",
            bestTimeToVisit = "October to February",
            attractions = listOf("The Shore Temple", "Pancha Rathas (Five Chariots)", "Arjuna's Penance rock carving", "Krishna's Butterball", "Mamallapuram Beach"),
            localFood = listOf("Fresh garlic prawns", "Masala grilled fish", "Madras filter coffee", "Spicy coastal calamari"),
            insiderTip = "Rent a local bicycle to explore the monuments at your own pace. Try visiting Arjuna's penance in the late afternoon to see the carvings bathed in warm golden twilight.",
            imageUrl = "https://images.unsplash.com/photo-1581455243305-645de4e45ded?auto=format&fit=crop&w=800&q=80",
            coordinates = "12.6191° N, 80.1941° E"
        ),
        Destination(
            id = "chennai",
            name = "Chennai",
            region = "Northern Coastal Coast",
            category = "Beaches & Coastal",
            summary = "Tamil Nadu's rich capital, featuring Marina Beach, ancient Dravidian temples, colonial remnants, and vibrant music cultures.",
            description = "Chennai, formerly Madras, balances urban sophistication with age-old traditions. From classical Carnatic music festivals and ancient Kapaleeshwarar temple spires to the longest natural beach in India, Chennai serves as the authentic gateway to Southern India.",
            bestTimeToVisit = "November to February",
            attractions = listOf("Marina Beach (Longest natural beach in India)", "Kapaleeshwarar Temple (Mylapore)", "Fort St. George & museum", "San Thome Cathedral Basilica"),
            localFood = listOf("Authentic Mylapore Filter Coffee", "Sambar Idli & Ghee Podi Dosa", "Sundal (temple-style seasoned chickpeas)", "Adyar bakery plum cake"),
            insiderTip = "Take an early morning stroll on Marina Beach (around 5:30 AM) to see fishermen landing their daily catch, combined with a peaceful local sunrise.",
            imageUrl = "https://images.unsplash.com/photo-1541123437800-1bb1317badc2?auto=format&fit=crop&w=800&q=80",
            coordinates = "13.0827° N, 80.2707° E"
        ),
        Destination(
            id = "kodaikanal",
            name = "Kodaikanal",
            region = "Western Ghats / Palani Hills",
            category = "Hill Stations",
            summary = "The 'Princess of Hill Stations' renowned for its star-shaped central lake, evergreen forests, and misty walking trails.",
            description = "Kodaikanal is a fresh mountainous refuge elevated at 2,000 meters. Cloaked in dense shola forests, pine grooves, cold waterfalls, and sweeping rock views, it offers a cool, peaceful alternative to Ooty.",
            bestTimeToVisit = "April to June & September to October",
            attractions = listOf("Kodaikanal Star Lake", "Coaker's Walk", "Pillar Rocks of Kodai", "Pine Forest Walk", "Bryant Botanical Park"),
            localFood = listOf("Fresh plums, pears, and passionfruit", "Homemade chocolate fudge", "Smoked local mountain cheese"),
            insiderTip = "Stroll along Coaker's Walk early in the morning to watch the clouds fill the mountain valley beneath you like a soft sea of cotton.",
            imageUrl = "https://images.unsplash.com/photo-1583212292454-1fe6229603b7?auto=format&fit=crop&w=800&q=80",
            coordinates = "10.2381° N, 77.4892° E"
        ),
        Destination(
            id = "rameshwaram",
            name = "Rameshwaram",
            region = "Pamban Island / Gulf of Mannar",
            category = "Beaches & Coastal",
            summary = "A sacred island town containing the colossal Ramanathaswamy corridor, surrounded by turquoise waters and ghost coastlines.",
            description = "Rameshwaram is a spiritual island connected to mainland India via the spectacular engineering marvel of the Pamban Bridge. A pilgrimage site steeped in history, it also features the quiet dunes of Dhanushkodi, where the Indian Ocean and Bay of Bengal meet.",
            bestTimeToVisit = "October to March",
            attractions = listOf("Ramanathaswamy Temple (Longest corridor in India)", "Pamban Railway Bridge", "Dhanushkodi Beach & Ghost Town", "APJ Abdul Kalam Memorial House"),
            localFood = listOf("Fish curry meals", "Vada, Idli & Sambhar", "Sweet Kumbakonam Degree Coffee"),
            insiderTip = "Take a local 4x4 or authorized vehicle to Dhanushkodi point in the morning; the beach is dramatically beautiful, showing ruins of a town reclaimed by the sea in 1964.",
            imageUrl = "https://images.unsplash.com/photo-1544498921-2993fe9624e7?auto=format&fit=crop&w=800&q=80",
            coordinates = "9.2876° N, 79.3129° E"
        ),
        Destination(
            id = "kanyakumari",
            name = "Kanyakumari",
            region = "Southernmost tip of mainland India",
            category = "Beaches & Coastal",
            summary = "The majestic meeting point of three major seas, offering dual sun rises and sunsets over the vast ocean.",
            description = "Kanyakumari is situated at the absolute tip of India's peninsula, where the Indian Ocean, Arabian Sea, and Bay of Bengal converge. Famous for its off-shore Vivekananda Rock Memorial and massive Thiruvalluvar stone monument, it offers unmatched oceanic viewpoints.",
            bestTimeToVisit = "October to March",
            attractions = listOf("Vivekananda Rock Memorial", "Thiruvalluvar Statue (133ft tall)", "Kanyakumari Sunset View Point", "Padmanabhapuram Palace (wooden palace nearby)"),
            localFood = listOf("Traditional fish meals", "Appam with stew", "Banana chips fried in coconut oil"),
            insiderTip = "On full moon evenings, you can experience the rare phenomenon of witnessing the sun sunset in the west while the moon simultaneously rises in the east over the horizon.",
            imageUrl = "https://images.unsplash.com/photo-1600100395861-5052d919c5a1?auto=format&fit=crop&w=800&q=80", // Using Madurai temple since Kanyakumari is heritage too
            coordinates = "8.0883° N, 77.5385° E"
        )
    )
}
