//
//  AppDelegate.swift
//  howmuch
//
//  Created by ljx on 2025/6/27.
//

import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let frame = UIScreen.main.bounds
        window = UIWindow(frame: frame)
        
        let tabBarController = TabBarController()
        let homeVC = UINavigationController(rootViewController: HomeViewController())
        let mineVC = UINavigationController(rootViewController: MineViewController())
        let everyDayVC = UINavigationController(rootViewController: EveryDayViewController())
        tabBarController.viewControllers = [homeVC, everyDayVC, mineVC]
        tabBarController.selectedIndex = tabBarController.viewControllers?.firstIndex(of: everyDayVC) ?? 0
        
        window?.rootViewController = tabBarController
        window?.makeKeyAndVisible()
        
        return true
    }
}

