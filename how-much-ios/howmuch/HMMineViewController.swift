//
//  HMMineViewController.swift
//  howmuch
//
//  Created by ljx on 2025/6/28.
//

import UIKit

final class HMMineViewController: UIViewController {
    
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
        view.backgroundColor = .clear
        tabBarItem = UITabBarItem(title: "我的", image: UIImage(systemName: "person"), tag:HMTabBarItemType.mine.rawValue)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        
    }
    
}
