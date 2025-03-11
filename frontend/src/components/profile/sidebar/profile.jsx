export const profile =[
    {
        heading: "My Account",
        links: [
            {
                name: "My Profile",
                link: "/profile",
            },
            {
                name: "Addresses",
                link: "/profile/address",
            },
            {
                name: "My Payment Options",
                link: "/profile/payment",
            },
            {
                name: "Account Security",
                link: "/profile/security",
            },
        ],
    },
    {
        heading: "My Orders",
        links: [
            {
                name: "All Orders",
                link: "/profile/orders",
                filter: "",
            },
            {
                name: "Paid Orders",
                link: "/profile/orders",
                filter: "PAID",
            },
            {
                name: "Unpaid Orders",
                link: "/profile/orders",
                filter: "UNPAID",
            },
            {
                name: "Processing Orders",
                link: "/profile/orders",
                filter: "PROCESSING",
            },
            {
                name: "Unprocessed Orders",
                link: "/profile/orders",
                filter: "NOT_PROCESSED",
            },
            {
                name: "Dispatched Orders",
                link: "/profile/orders",
                filter: "DISPATCHED",
            },
            {
                name: "Delivered Orders",
                link: "/profile/orders",
                filter: "COMPLETED",
            },
            {
                name: "Cancelled Orders",
                link: "/profile/orders",
                filter: "CANCELED",
            },
        ],
    },
    {
        heading: "My Lists",
        links: [
            {
                name: "Whishlist",
                link: "/profile/whishlist",
            },
            {
                name: "Recently Viewed",
                link: "/profile/recent",
            },
        ],
    },
    {
        heading: "Customer Service",
        links: [
            {
                name: "My Message",
                link: "/profile/messages",
            },
            {
                name: "Service Records",
                link: "/profile/services",
            },
        ],
    },
    {
        heading: "Policy",
        links: [
            {
                name: "My Message",
                link: "/profile/messages",
            },
            {
                name: "Service Records",
                link: "/profile/services",
            },
        ],
    },
    {
        heading: "Sign Out",
        link: []
    },
];

