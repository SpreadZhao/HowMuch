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
        
        let tabBarController = HMTabBarController()
        let vc1 = UINavigationController(rootViewController: HMHomeViewController())
        let vc2 = UINavigationController(rootViewController: HMMineViewController())
        tabBarController.viewControllers = [vc1, vc2]
        
        window?.rootViewController = tabBarController
        window?.makeKeyAndVisible()
        
        return true
    }
}

