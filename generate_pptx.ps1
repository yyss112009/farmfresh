param()

# Ensure clean UTF-8 encoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$pptApp = New-Object -ComObject PowerPoint.Application
$pptApp.Visible = [Microsoft.Office.Core.MsoTriState]::msoTrue

$pres = $pptApp.Presentations.Add([Microsoft.Office.Core.MsoTriState]::msoTrue)
# Widescreen 16:9
$pres.PageSetup.SlideWidth = 960
$pres.PageSetup.SlideHeight = 540

# Color Constants (RGB values)
$cDarkGreen  = 22 + (92 * 256) + (45 * 65536)     # #165C2D
$cForest     = 27 + (94 * 256) + (32 * 65536)     # #1B5E20
$cTurmeric   = 61 + (163 * 256) + (232 * 65536)   # #E8A33D
$cInk        = 31 + (42 * 256) + (26 * 65536)     # #1F2A1A
$cLightBg    = 244 + (248 * 256) + (244 * 65536) # #F4F8F4
$cGoldBg     = 240 + (248 * 256) + (254 * 65536) # #FEF8F0
$cWhite      = 255 + (255 * 256) + (255 * 65536)
$cGrayText   = 90 + (100 * 256) + (90 * 65536)
$cRed        = 40 + (40 * 256) + (200 * 65536)

function Add-Header {
    param($slide, [string]$badgeText, [string]$titleText, [string]$subtitleText)

    $topLine = $slide.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRectangle, 0, 0, 960, 6)
    $topLine.Fill.Solid()
    $topLine.Fill.ForeColor.RGB = $cTurmeric
    $topLine.Line.Visible = [Microsoft.Office.Core.MsoTriState]::msoFalse

    $badge = $slide.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 40, 20, 240, 24)
    $badge.Fill.Solid()
    $badge.Fill.ForeColor.RGB = $cLightBg
    $badge.Line.ForeColor.RGB = $cForest
    $badge.Line.Weight = 1
    $badge.TextFrame.TextRange.Text = $badgeText.ToUpper()
    $badge.TextFrame.TextRange.Font.Size = 10
    $badge.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
    $badge.TextFrame.TextRange.Font.Color.RGB = $cForest
    $badge.TextFrame.TextRange.ParagraphFormat.Alignment = [Microsoft.Office.Interop.PowerPoint.PpParagraphAlignment]::ppAlignCenter

    $title = $slide.Shapes.AddTextBox([Microsoft.Office.Core.MsoTextOrientation]::msoTextOrientationHorizontal, 40, 48, 880, 42)
    $title.TextFrame.TextRange.Text = $titleText
    $title.TextFrame.TextRange.Font.Name = "Georgia"
    $title.TextFrame.TextRange.Font.Size = 23
    $title.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
    $title.TextFrame.TextRange.Font.Color.RGB = $cDarkGreen

    if ($subtitleText) {
        $sub = $slide.Shapes.AddTextBox([Microsoft.Office.Core.MsoTextOrientation]::msoTextOrientationHorizontal, 40, 90, 880, 26)
        $sub.TextFrame.TextRange.Text = $subtitleText
        $sub.TextFrame.TextRange.Font.Name = "Calibri"
        $sub.TextFrame.TextRange.Font.Size = 13
        $sub.TextFrame.TextRange.Font.Color.RGB = $cGrayText
    }
}

function Add-MarathishBox {
    param($slide, [int]$top, [string]$text)
    $box = $slide.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 40, $top, 880, 52)
    $box.Fill.Solid()
    $box.Fill.ForeColor.RGB = $cGoldBg
    $box.Line.ForeColor.RGB = $cTurmeric
    $box.Line.Weight = 1.5
    $box.TextFrame.TextRange.Text = $text
    $box.TextFrame.TextRange.Font.Name = "Calibri"
    $box.TextFrame.TextRange.Font.Size = 11.5
    $box.TextFrame.TextRange.Font.Color.RGB = $cInk
    $box.TextFrame.MarginLeft = 14
    $box.TextFrame.MarginRight = 14
}

# ==============================================================================
# SLIDE 1: Title Slide
# ==============================================================================
$s1 = $pres.Slides.Add(1, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
$bg1 = $s1.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRectangle, 0, 0, 960, 540)
$bg1.Fill.Solid(); $bg1.Fill.ForeColor.RGB = $cForest; $bg1.Line.Visible = [Microsoft.Office.Core.MsoTriState]::msoFalse

$card1 = $s1.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 50, 40, 860, 460)
$card1.Fill.Solid(); $card1.Fill.ForeColor.RGB = $cWhite; $card1.Line.Visible = [Microsoft.Office.Core.MsoTriState]::msoFalse

$t1 = $s1.Shapes.AddTextBox([Microsoft.Office.Core.MsoTextOrientation]::msoTextOrientationHorizontal, 80, 65, 800, 50)
$t1.TextFrame.TextRange.Text = "FarmFresh"
$t1.TextFrame.TextRange.Font.Name = "Georgia"
$t1.TextFrame.TextRange.Font.Size = 38
$t1.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$t1.TextFrame.TextRange.Font.Color.RGB = $cDarkGreen

$t1sub = $s1.Shapes.AddTextBox([Microsoft.Office.Core.MsoTextOrientation]::msoTextOrientationHorizontal, 80, 115, 800, 35)
$t1sub.TextFrame.TextRange.Text = "Direct Farmer-to-Consumer Agri-Ecommerce Platform"
$t1sub.TextFrame.TextRange.Font.Name = "Calibri"
$t1sub.TextFrame.TextRange.Font.Size = 18
$t1sub.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$t1sub.TextFrame.TextRange.Font.Color.RGB = $cTurmeric

Add-MarathishBox -slide $s1 -top 165 -text "Project Vision (Marathi): FarmFresh ha ek direct online marketplace ahe jithe Shetkari (Farmer) aapan pikawolela taaza maal kontech dalal (middlemen) nastana direct Grahak (Customer) la viku shakto. Shetkaryala purna nafa milto ani grahakala taaja maal kami daamat milto."

$p1 = $s1.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 80, 235, 250, 230)
$p1.Fill.Solid(); $p1.Fill.ForeColor.RGB = $cLightBg; $p1.Line.ForeColor.RGB = $cForest
$p1.TextFrame.TextRange.Text = "Farmer Empowerment`n`n- Fair and direct pricing`n- Digital produce catalog`n- Direct order accept / cancel`n- Zero broker commission`n- Complete revenue ownership"
$p1.TextFrame.TextRange.Font.Size = 13; $p1.TextFrame.TextRange.Font.Color.RGB = $cInk

$p2 = $s1.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 355, 235, 250, 230)
$p2.Fill.Solid(); $p2.Fill.ForeColor.RGB = $cLightBg; $p2.Line.ForeColor.RGB = $cForest
$p2.TextFrame.TextRange.Text = "Consumer Freshness`n`n- Pure organic farm produce`n- Live dynamic cart`n- Verified doorstep delivery`n- Transparent price breakdown`n- Order status tracking"
$p2.TextFrame.TextRange.Font.Size = 13; $p2.TextFrame.TextRange.Font.Color.RGB = $cInk

$p3 = $s1.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 630, 235, 250, 230)
$p3.Fill.Solid(); $p3.Fill.ForeColor.RGB = $cLightBg; $p3.Line.ForeColor.RGB = $cForest
$p3.TextFrame.TextRange.Text = "Platform Moderation`n`n- Farmer account verification`n- Produce quality inspection`n- Global order monitoring`n- Customer inquiry resolver`n- Complete admin control"
$p3.TextFrame.TextRange.Font.Size = 13; $p3.TextFrame.TextRange.Font.Color.RGB = $cInk

# ==============================================================================
# SLIDE 2: Problem vs Solution
# ==============================================================================
$s2 = $pres.Slides.Add(2, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s2 -badgeText "Problem vs Solution" -titleText "Why FarmFresh? (Problem and Solution)" -subtitleText "Bridging the critical supply chain gap in agricultural trading."
Add-MarathishBox -slide $s2 -top 125 -text "Samasya Kay Hoti? Traditional bajarat Dalal 30% te 50% commission ghetat, jyana shetkaryala kami bhav milto ani maal pocheparyant shila hoto. FarmFresh he dalal completely hatavto!"

$boxLeft = $s2.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 40, 190, 425, 320)
$boxLeft.Fill.Solid(); $boxLeft.Fill.ForeColor.RGB = 250 + (240*256) + (240*65536); $boxLeft.Line.ForeColor.RGB = $cRed
$boxLeft.TextFrame.TextRange.Text = "Traditional Market Problems:`n`n" +
"- Heavy Brokerage: Agents take 30% to 50% cuts from produce revenue.`n`n" +
"- Stale Produce: Harvest takes days in mandis before reaching retail stores.`n`n" +
"- No Digital Access: Small rural farmers cannot reach urban buyers directly.`n`n" +
"- Unfair Price Volatility: Wholesale cartels artificially depress farmer selling prices."
$boxLeft.TextFrame.TextRange.Font.Size = 13; $boxLeft.TextFrame.TextRange.Font.Color.RGB = $cInk

$boxRight = $s2.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 495, 190, 425, 320)
$boxRight.Fill.Solid(); $boxRight.Fill.ForeColor.RGB = $cLightBg; $boxRight.Line.ForeColor.RGB = $cForest
$boxRight.TextFrame.TextRange.Text = "FarmFresh Digital Solution:`n`n" +
"- Zero Commission: 100% direct pricing decided directly by the farmer.`n`n" +
"- Farm-to-Fork Freshness: Produce harvested on order and delivered fresh.`n`n" +
"- Digital Farmer Portal: Responsive web dashboard to list crops and track orders.`n`n" +
"- Real-time Order Control: Live Pending, Confirmed, Shipped, Delivered workflow."
$boxRight.TextFrame.TextRange.Font.Size = 13; $boxRight.TextFrame.TextRange.Font.Color.RGB = $cInk

# ==============================================================================
# SLIDE 3: Tech Stack & Versions
# ==============================================================================
$s3 = $pres.Slides.Add(3, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s3 -badgeText "Technology Stack" -titleText "Technology Stack and Exact Versions" -subtitleText "Enterprise-grade architecture built with Java, Spring Boot, and MySQL."
Add-MarathishBox -slide $s3 -top 125 -text "Tech Stack Summary: Backend madhe Spring Boot 3.5.5 ani Java 25 ahe, Database madhe MySQL 8.0 ahe, ani Frontend HTML5, CSS3, JavaScript ES6 ani Bootstrap 5.3.8 var tayar kelela ahe."

$tbl3 = $s3.Shapes.AddTable(7, 4, 40, 190, 880, 310).Table
$headers3 = @("Layer", "Technology", "Exact Version", "Core Role and Importance")
for ($col=0; $col -lt 4; $col++) {
    $tbl3.Cell(1, $col+1).Shape.TextFrame.TextRange.Text = $headers3[$col]
    $tbl3.Cell(1, $col+1).Shape.Fill.Solid()
    $tbl3.Cell(1, $col+1).Shape.Fill.ForeColor.RGB = $cForest
    $tbl3.Cell(1, $col+1).Shape.TextFrame.TextRange.Font.Color.RGB = $cWhite
    $tbl3.Cell(1, $col+1).Shape.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
}
$rows3 = @(
    @("Runtime", "Java OpenJDK", "25.0.2 (LTS)", "High performance, virtual threads, robust memory safety"),
    @("Backend Framework", "Spring Boot", "3.5.5", "REST API controllers, auto-configuration, dependency injection"),
    @("ORM / Persistence", "Hibernate / JPA", "6.6.26.Final", "Entity mapping, transaction management and type-safe queries"),
    @("Database Server", "MySQL Community", "8.0.46", "ACID compliant relational storage with ON DELETE SET NULL rules"),
    @("Embedded Server", "Apache Tomcat", "10.1.44", "High-throughput embedded servlet engine running on port 8080"),
    @("Frontend UI", "Bootstrap and Vanilla JS", "5.3.8 / ES6+", "Mobile-first responsive grid, modals, badges, and async fetch()")
)
for ($r=0; $r -lt $rows3.Length; $r++) {
    for ($c=0; $c -lt 4; $c++) {
        $cell = $tbl3.Cell($r+2, $c+1).Shape
        $cell.TextFrame.TextRange.Text = $rows3[$r][$c]
        $cell.TextFrame.TextRange.Font.Size = 12
        $cell.TextFrame.TextRange.Font.Color.RGB = $cInk
    }
}

# ==============================================================================
# SLIDE 4: Architecture & Data Flow
# ==============================================================================
$s4 = $pres.Slides.Add(4, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s4 -badgeText "System Design" -titleText "3-Tier System Architecture and Data Flow" -subtitleText "Modular separation of Client, REST API Controllers, Services, and MySQL."
Add-MarathishBox -slide $s4 -top 125 -text "Kasa Chalto Flow? User browser madhun action gheto -> api.js HTTP REST call karto -> Controller request Service layer kade pathavto -> JPA dwara MySQL database madhe data save kiva retrieve hoto."

$flowBox = $s4.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRectangle, 40, 190, 880, 310)
$flowBox.Fill.Solid(); $flowBox.Fill.ForeColor.RGB = 25 + (35*256) + (25*65536); $flowBox.Line.Visible = [Microsoft.Office.Core.MsoTriState]::msoFalse
$flowBox.TextFrame.TextRange.Font.Name = "Consolas"
$flowBox.TextFrame.TextRange.Font.Size = 11.5
$flowBox.TextFrame.TextRange.Font.Color.RGB = 200 + (240*256) + (200*65536)
$flowBox.TextFrame.TextRange.Text = @"
 [ Browser Client (HTML5 / CSS3 / ES6 api.js) ]
         |  ^
         |  |   HTTP / JSON REST Requests & Responses
         v  |
 [ Spring Boot Controller Layer (@RestController) ]
         |-- AuthController       (/api/auth)      -> Login, Register & Token dispatch
         |-- CustomerController   (/api/customer)  -> Profile & customer actions
         |-- FarmerController     (/api/farmer)    -> Product CRUD, Orders & Profile
         |-- AdminController      (/api/admin)     -> Farmer & Product moderation, Global Orders
         \-- OrderController      (/api/orders)    -> Order checkout & tracking
         |  ^
         v  |
 [ Business Service Layer (@Service) ]
         |-- AuthService, OrderService, ProductService, AdminService, FarmerProfileService
         |  ^
         v  |
 [ Data Access Layer (@Repository - Spring Data JPA) ]
         \-- CustomerRepository, FarmerRepository, ProductRepository, OrderRepository, OrderItemRepository
         |  ^
         v  |   Optimized JDBC Queries via HikariCP Pool
 [ MySQL 8.0 Database (farmfresh) ]
"@

# ==============================================================================
# SLIDE 5: Database Schema & Entities
# ==============================================================================
$s5 = $pres.Slides.Add(5, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s5 -badgeText "Database Architecture" -titleText "Database Schema and Entity Relationships" -subtitleText "Normalized schema with historical snapshots and cascade safety."
Add-MarathishBox -slide $s5 -top 125 -text "Database Design: customers, farmers, products, orders, order_items tables ahet. order_items madhe historical snapshot aslyamule product nantar delete kela tari juni order history safe rahate."

$entities = @(
    @("customers", "id (PK), full_name, email (UK), password (BCrypt), mobile, city, state, status"),
    @("farmers", "id (PK), full_name, email (UK), password, farm_name, mobile, farm_address, city, pincode, status"),
    @("products", "id (PK), farmer_id (FK), name, category, price, quantity, unit, image_url (LONGTEXT), status"),
    @("orders", "id (PK), customer_id (FK), name, phone, email, address, city, state, pin, subtotal, delivery_fee, total, status"),
    @("order_items", "id (PK), order_id (FK), product_id (FK_NULL), product_name, farmer_name, price, quantity, unit"),
    @("contact_messages", "id (PK), name, email, subject, message, created_at, status")
)

for ($i=0; $i -lt 6; $i++) {
    $col = $i % 3
    $row = [Math]::Floor($i / 3)
    $x = 40 + ($col * 300)
    $y = 190 + ($row * 155)
    $c = $s5.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, $x, $y, 280, 140)
    $c.Fill.Solid(); $c.Fill.ForeColor.RGB = $cLightBg; $c.Line.ForeColor.RGB = $cForest
    $c.TextFrame.TextRange.Text = "[$($entities[$i][0])]`n`n$($entities[$i][1])"
    $c.TextFrame.TextRange.Font.Size = 11.5
    $c.TextFrame.TextRange.Font.Color.RGB = $cInk
}

# ==============================================================================
# SLIDE 6: Customer Module Flow
# ==============================================================================
$s6 = $pres.Slides.Add(6, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s6 -badgeText "Customer Portal" -titleText "Customer Experience and Storefront Flow" -subtitleText "Frictionless discovery, shopping, and verified checkout."
Add-MarathishBox -slide $s6 -top 125 -text "Grahak Kasa Kharedi Karto? frounted.html varun bhaji-pala shodhto -> ourproduct.html varun Add to Cart karto -> mycard.html var quantity badalto -> check.html var delivery address takun order place karto."

$cFlow = @(
    @("1. Browse Catalog", "frounted.html & ourproduct.html", "Search produce in real time and filter by categories: Vegetables, Fruits, Grains, and Spices with live stock status."),
    @("2. Dynamic Cart", "mycard.html", "Add produce to basket with live count badge, adjust +/- quantities, calculate line totals, subtotal, and exact delivery fees."),
    @("3. Doorstep Checkout", "check.html", "Enter verified shipping address, contact phone, select delivery speed, and review final financial breakdown before placing order.")
)
for ($i=0; $i -lt 3; $i++) {
    $x = 40 + ($i * 300)
    $c = $s6.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, $x, 200, 280, 290)
    $c.Fill.Solid(); $c.Fill.ForeColor.RGB = $cLightBg; $c.Line.ForeColor.RGB = $cForest
    $c.TextFrame.TextRange.Text = "$($cFlow[$i][0])`n`n[$($cFlow[$i][1])]`n`n$($cFlow[$i][2])"
    $c.TextFrame.TextRange.Font.Size = 13.5
    $c.TextFrame.TextRange.Font.Color.RGB = $cInk
}

# ==============================================================================
# SLIDE 7: Cart Calculation Logic
# ==============================================================================
$s7 = $pres.Slides.Add(7, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s7 -badgeText "Core Business Logic" -titleText "Cart Calculation and Financial Math Rules" -subtitleText "Precise client-side arithmetic for transparent billing."
Add-MarathishBox -slide $s7 -top 125 -text "Total Kashi Calculate Hote? Pratyek item cha (Price x Quantity) = Line Total. Sarvanchi beej = Subtotal. Tyat Rs. 30 Delivery Fee add houn Grand Total banto."

$codeCard = $s7.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRectangle, 40, 190, 880, 310)
$codeCard.Fill.Solid(); $codeCard.Fill.ForeColor.RGB = 25 + (35*256) + (25*65536); $codeCard.Line.Visible = [Microsoft.Office.Core.MsoTriState]::msoFalse
$codeCard.TextFrame.TextRange.Font.Name = "Consolas"
$codeCard.TextFrame.TextRange.Font.Size = 12.5
$codeCard.TextFrame.TextRange.Font.Color.RGB = 210 + (240*256) + (210*65536)
$codeCard.TextFrame.TextRange.Text = @"
 // 1. Line Total Calculation
 const lineTotal = item.price * item.quantity;

 // 2. Subtotal (Sum of all line items)
 let subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

 // 3. Delivery Fee Rule (Added only if cart has active items)
 const DELIVERY_FEE = cart.length > 0 ? 30.00 : 0.00;

 // 4. Grand Total Amount
 const grandTotal = subtotal + DELIVERY_FEE;

 // 5. DOM Updates on mycard.html
 document.getElementById("subtotalValue").textContent = "Rs. " + Math.round(subtotal);
 document.getElementById("deliveryValue").textContent = "Rs. " + (cart.length > 0 ? DELIVERY_FEE : 0);
 document.getElementById("totalValue").textContent    = "Rs. " + Math.round(grandTotal);
"@

# ==============================================================================
# SLIDE 8: Farmer Produce Management
# ==============================================================================
$s8 = $pres.Slides.Add(8, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s8 -badgeText "Farmer Portal" -titleText "Farmer Produce Management and Listing" -subtitleText "Enabling farmers to act as independent digital shop owners."
Add-MarathishBox -slide $s8 -top 125 -text "Shetkari Kasa Produce List Karto? Shetkari farmer dashboard madhun add12.html var jato, bhaji/falaanche photo upload karto, kg chi kimmat tharvato ani stock save karto."

$fCards = @(
    @("Add Produce", "add12.html", "List new produce with Crop Name, Category, Price per kg/unit, stock count, and direct Base64 high-resolution photo uploads."),
    @("Manage Produce", "manageproduct.html", "Adjust dynamic pricing according to current market rates, update available stock quantities, and remove old items safely."),
    @("Farm Profile", "profile.html", "Manage registered Farm Name, primary mobile contact, farm address, city, state, and pincode credentials.")
)
for ($i=0; $i -lt 3; $i++) {
    $x = 40 + ($i * 300)
    $c = $s8.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, $x, 200, 280, 290)
    $c.Fill.Solid(); $c.Fill.ForeColor.RGB = $cLightBg; $c.Line.ForeColor.RGB = $cForest
    $c.TextFrame.TextRange.Text = "$($fCards[$i][0])`n`n[$($fCards[$i][1])]`n`n$($fCards[$i][2])"
    $c.TextFrame.TextRange.Font.Size = 13.5
    $c.TextFrame.TextRange.Font.Color.RGB = $cInk
}

# ==============================================================================
# SLIDE 9: Farmer Order Management
# ==============================================================================
$s9 = $pres.Slides.Add(9, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s9 -badgeText "Order Fulfillment" -titleText "Farmer Order Management -- Accept and Cancel" -subtitleText "Instant customer detail inspection and one-click order fulfillment."
Add-MarathishBox -slide $s9 -top 125 -text "Shetkari Order Kashi Manage Karto? orders.html var shetkari 'View Details' var click karun customer cha number ani address baghu shakto, ani Green Accept Button dabun order confirm karu shakto."

$fOrderActions = @(
    @("View Details Modal", "Inspect full customer phone number (clickable tel:), email, complete home shipping address, city, state, pin, and ordered produce summary in a popup dialog."),
    @("Accept Order (Green Button)", "Single-click confirmation button in row or modal that immediately updates the order status to 'Confirmed' for packaging and harvesting."),
    @("Cancel Order (Red Button)", "Safe cancellation button with user confirmation prompt that marks the order status as 'Cancelled' if produce is out of stock.")
)
for ($i=0; $i -lt 3; $i++) {
    $x = 40 + ($i * 300)
    $c = $s9.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, $x, 200, 280, 290)
    $c.Fill.Solid(); $c.Fill.ForeColor.RGB = $cLightBg; $c.Line.ForeColor.RGB = $cForest
    $c.TextFrame.TextRange.Text = "$($fOrderActions[$i][0])`n`n$($fOrderActions[$i][1])"
    $c.TextFrame.TextRange.Font.Size = 13.5
    $c.TextFrame.TextRange.Font.Color.RGB = $cInk
}

# ==============================================================================
# SLIDE 10: Admin Dashboard & Moderation
# ==============================================================================
$s10 = $pres.Slides.Add(10, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s10 -badgeText "Administration" -titleText "Admin Dashboard and Platform Moderation" -subtitleText "Central quality governance, dispute resolution, and analytics."
Add-MarathishBox -slide $s10 -top 125 -text "Admin Cha Role Kay Ahe? Admin poorna platform var laksha thevto. Khote shetkari block karne, product chi quality check karne ani sarv orders cha hishob thevne."

$aCards = @(
    @("Order Oversight (order.html)", "Supervise all customer orders placed across all farmers with full financial breakdown, customer contact details, and manual status overrides."),
    @("Farmer Moderation (farmer.html)", "Review newly registered farmers, verify farm authenticity, view individual farmer sales metrics, and approve or ban accounts."),
    @("Produce Quality (product.html)", "Moderate produce listings with 'ok' or 'blocked' flags to prevent out-of-spec products from appearing on the public shop."),
    @("Inquiry Center (msg.html)", "Review incoming customer inquiries submitted from contact.html and manage customer support tickets.")
)
for ($i=0; $i -lt 4; $i++) {
    $col = $i % 2
    $row = [Math]::Floor($i / 2)
    $x = 40 + ($col * 450)
    $y = 200 + ($row * 150)
    $c = $s10.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, $x, $y, 430, 135)
    $c.Fill.Solid(); $c.Fill.ForeColor.RGB = $cLightBg; $c.Line.ForeColor.RGB = $cForest
    $c.TextFrame.TextRange.Text = "$($aCards[$i][0])`n`n$($aCards[$i][1])"
    $c.TextFrame.TextRange.Font.Size = 12.5
    $c.TextFrame.TextRange.Font.Color.RGB = $cInk
}

# ==============================================================================
# SLIDE 11: Security & RBAC
# ==============================================================================
$s11 = $pres.Slides.Add(11, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s11 -badgeText "Security Architecture" -titleText "Security and Role-Based Access Control (RBAC)" -subtitleText "Zero-trust token verification and BCrypt password encryption."
Add-MarathishBox -slide $s11 -top 125 -text "Surakshitata (Security): Passwords BCrypt algorithm ne encrypt kele jataat. Farmer fakt tyachya portal madhe jau shakto, ani Customer fakt customer pages access karu shakto."

$tbl11 = $s11.Shapes.AddTable(4, 3, 40, 195, 880, 290).Table
$h11 = @("Security Measure", "Technical Implementation", "Security Benefit / Protection")
for ($col=0; $col -lt 3; $col++) {
    $tbl11.Cell(1, $col+1).Shape.TextFrame.TextRange.Text = $h11[$col]
    $tbl11.Cell(1, $col+1).Shape.Fill.Solid()
    $tbl11.Cell(1, $col+1).Shape.Fill.ForeColor.RGB = $cForest
    $tbl11.Cell(1, $col+1).Shape.TextFrame.TextRange.Font.Color.RGB = $cWhite
    $tbl11.Cell(1, $col+1).Shape.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
}
$rows11 = @(
    @("Password Encryption", "BCryptPasswordEncoder (Salted Hash)", "Passwords cannot be decrypted even if database is exposed."),
    @("Role Verification", "AuthService.requireRole(token, role)", "Strict RBAC ensures farmers cannot call admin APIs and vice versa."),
    @("Dynamic Navbar Auth", "api.js (ffInitNavbarAuth)", "Replaces 'Login' with 'Logout (User Name)' dynamically across all pages.")
)
for ($r=0; $r -lt $rows11.Length; $r++) {
    for ($c=0; $c -lt 3; $c++) {
        $cell = $tbl11.Cell($r+2, $c+1).Shape
        $cell.TextFrame.TextRange.Text = $rows11[$r][$c]
        $cell.TextFrame.TextRange.Font.Size = 13
        $cell.TextFrame.TextRange.Font.Color.RGB = $cInk
    }
}

# ==============================================================================
# SLIDE 12: Challenges Solved
# ==============================================================================
$s12 = $pres.Slides.Add(12, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s12 -badgeText "Engineering Triumphs" -titleText "Key Technical Challenges and Fixes Applied" -subtitleText "Root cause diagnosis and robust architectural resolutions."
Add-MarathishBox -slide $s12 -top 125 -text "Taantrik Adchani and Solution: Photo save kartana 'Data too long' error yet hota to LONGTEXT karun solve kela. Product delete kartana FK error yet hota to ON DELETE SET NULL karun solve kela."

$ch1 = $s12.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 40, 195, 425, 305)
$ch1.Fill.Solid(); $ch1.Fill.ForeColor.RGB = $cLightBg; $ch1.Line.ForeColor.RGB = $cForest
$ch1.TextFrame.TextRange.Text = "1. Image Data Truncation Error`n`n" +
"- Problem: Camera Base64 image strings exceeded 255 chars in VARCHAR(255).`n`n" +
"- Impact: MySQL threw 'Data truncation: Data too long for column image_url'.`n`n" +
"- Fix: Modified MySQL column to LONGTEXT and annotated @Lob in Product.java entity."
$ch1.TextFrame.TextRange.Font.Size = 13; $ch1.TextFrame.TextRange.Font.Color.RGB = $cInk

$ch2 = $s12.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 495, 195, 425, 305)
$ch2.Fill.Solid(); $ch2.Fill.ForeColor.RGB = $cLightBg; $ch2.Line.ForeColor.RGB = $cForest
$ch2.TextFrame.TextRange.Text = "2. Foreign Key Cascade Constraint Crash`n`n" +
"- Problem: Deleting ordered products threw MySQL RESTRICT FK constraint error.`n`n" +
"- Impact: Farmers were blocked from removing discontinued products from catalog.`n`n" +
"- Fix: Configured ON DELETE SET NULL on DB and unlinked via nullifyProductReference()."
$ch2.TextFrame.TextRange.Font.Size = 13; $ch2.TextFrame.TextRange.Font.Color.RGB = $cInk

# ==============================================================================
# SLIDE 13: Future Roadmap
# ==============================================================================
$s13 = $pres.Slides.Add(13, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s13 -badgeText "Future Roadmap" -titleText "Future Scope and Innovations" -subtitleText "Scaling FarmFresh into a comprehensive national agri-tech ecosystem."
Add-MarathishBox -slide $s13 -top 125 -text "Pudhil Features: Online payment (UPI/GPay), WhatsApp var order alert message, ani Shetkaryanna yogya bhav milnyasathi AI Crop Price Recommendation system."

$futureCards = @(
    @("Online Payment Gateway", "Integration with Razorpay, UPI QR Codes, and Net Banking for instant automated farmer bank payouts."),
    @("WhatsApp / SMS Alerts", "Automated WhatsApp alerts to farmers whenever a new order is received with direct accept links."),
    @("AI Price Recommendation", "Machine learning price prediction using mandi rates and weather data to suggest optimal harvest prices.")
)
for ($i=0; $i -lt 3; $i++) {
    $x = 40 + ($i * 300)
    $c = $s13.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, $x, 200, 280, 290)
    $c.Fill.Solid(); $c.Fill.ForeColor.RGB = $cLightBg; $c.Line.ForeColor.RGB = $cForest
    $c.TextFrame.TextRange.Text = "$($futureCards[$i][0])`n`n$($futureCards[$i][1])"
    $c.TextFrame.TextRange.Font.Size = 13.5
    $c.TextFrame.TextRange.Font.Color.RGB = $cInk
}

# ==============================================================================
# SLIDE 14: Conclusion
# ==============================================================================
$s14 = $pres.Slides.Add(14, [Microsoft.Office.Interop.PowerPoint.PpSlideLayout]::ppLayoutBlank)
Add-Header -slide $s14 -badgeText "Conclusion" -titleText "Project Conclusion and Final Summary" -subtitleText "A sustainable, production-grade technology bridge for agriculture."
Add-MarathishBox -slide $s14 -top 125 -text "Antim Nishkarsha (Conclusion): FarmFresh ha project shetkaryanchya aarthik vikaasasathi ani grahakanchya aarogyasathi ek uttam madhyam ahe. Modern Java 25, Spring Boot 3.5, MySQL ani Bootstrap chya madhyamatun ha ek 100% working, secure ani production-ready project banavla ahe."

$sum1 = $s14.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 80, 200, 250, 160)
$sum1.Fill.Solid(); $sum1.Fill.ForeColor.RGB = $cLightBg; $sum1.Line.ForeColor.RGB = $cForest
$sum1.TextFrame.TextRange.Text = "0%`n`nMiddlemen Brokerage"
$sum1.TextFrame.TextRange.Font.Size = 16; $sum1.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue; $sum1.TextFrame.TextRange.Font.Color.RGB = $cDarkGreen

$sum2 = $s14.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 355, 200, 250, 160)
$sum2.Fill.Solid(); $sum2.Fill.ForeColor.RGB = $cLightBg; $sum2.Line.ForeColor.RGB = $cForest
$sum2.TextFrame.TextRange.Text = "100%`n`nDirect Harvest Freshness"
$sum2.TextFrame.TextRange.Font.Size = 16; $sum2.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue; $sum2.TextFrame.TextRange.Font.Color.RGB = $cDarkGreen

$sum3 = $s14.Shapes.AddShape([Microsoft.Office.Core.MsoAutoShapeType]::msoShapeRoundedRectangle, 630, 200, 250, 160)
$sum3.Fill.Solid(); $sum3.Fill.ForeColor.RGB = $cLightBg; $sum3.Line.ForeColor.RGB = $cForest
$sum3.TextFrame.TextRange.Text = "14 Slides`n`nComplete Project Deck"
$sum3.TextFrame.TextRange.Font.Size = 16; $sum3.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue; $sum3.TextFrame.TextRange.Font.Color.RGB = $cDarkGreen

$ty = $s14.Shapes.AddTextBox([Microsoft.Office.Core.MsoTextOrientation]::msoTextOrientationHorizontal, 80, 390, 800, 70)
$ty.TextFrame.TextRange.Text = "Thank You! / Dhanyawad!`nQuestions and Answers Open"
$ty.TextFrame.TextRange.Font.Name = "Georgia"
$ty.TextFrame.TextRange.Font.Size = 22
$ty.TextFrame.TextRange.Font.Bold = [Microsoft.Office.Core.MsoTriState]::msoTrue
$ty.TextFrame.TextRange.Font.Color.RGB = $cDarkGreen
$ty.TextFrame.TextRange.ParagraphFormat.Alignment = [Microsoft.Office.Interop.PowerPoint.PpParagraphAlignment]::ppAlignCenter

# ==============================================================================
# Save Presentation
# ==============================================================================
$outputPptx = "E:\Farm-Fresh\FarmFresh_Presentation.pptx"
if (Test-Path $outputPptx) {
    Remove-Item $outputPptx -Force
}
$pres.SaveAs($outputPptx)
$pres.Close()
$pptApp.Quit()
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($pres) | Out-Null
[System.Runtime.InteropServices.Marshal]::ReleaseComObject($pptApp) | Out-Null

Write-Output "SUCCESS: Created $outputPptx"
